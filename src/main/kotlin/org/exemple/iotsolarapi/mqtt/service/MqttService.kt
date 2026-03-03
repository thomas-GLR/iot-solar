package org.exemple.iotsolarapi.mqtt.service

import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.exemple.iotsolarapi.resistanceStates.service.ResistanceAckNotifier
import org.exemple.iotsolarapi.resistanceStates.service.ResistanceStateService
import org.exemple.iotsolarapi.temperatures.service.TemperatureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class MqttService(
    private val mqttClient: IMqttClient,
    private val temperatureService: TemperatureService,
    private val resistanceAckNotifier: ResistanceAckNotifier,
    private val resistanceStateService: ResistanceStateService
) {

    private val logger: Logger = LoggerFactory.getLogger(MqttService::class.java)

    private val TOPIC_TEMPERATURE_TOP = "iotsolar/temperature/top"
    private val TOPIC_TEMPERATURE_MIDDLE = "iotsolar/temperature/middle"
    private val TOPIC_TEMPERATURE_BOTTOM = "iotsolar/temperature/bottom"

    private val TOPIC_RESISTANCE_ACK = "iotsolar/resistance/ack"

    /**
     * Subscribe to topic when mqtt Client is created
     */
    @EventListener(ApplicationReadyEvent::class)
    fun subscribeToTopics() {
        val topicByReadingDeviceName = mutableMapOf<ReadingDeviceName, String>()

        topicByReadingDeviceName.put(ReadingDeviceName.TOP, TOPIC_TEMPERATURE_TOP)
        topicByReadingDeviceName.put(ReadingDeviceName.MIDDLE, TOPIC_TEMPERATURE_MIDDLE)
        topicByReadingDeviceName.put(ReadingDeviceName.BOTTOM, TOPIC_TEMPERATURE_BOTTOM)

        if (mqttClient.isConnected) {
            topicByReadingDeviceName.forEach { (readingDeviceName, topic) ->
                try {
                    logger.info("Souscription au topic: {}", topic)

                    mqttClient.subscribe(topic, 1) { topicName, message ->
                        logger.info("Message reçu sur {}: {}", topicName, String(message.payload))
                        handleTopicTemperature(topicName, message, readingDeviceName)
                    }
                } catch (e: Exception) {
                    logger.error("Erreur lors de la souscription au topic {}: {}", topic, e.message)
                }
            }

            mqttClient.subscribe(TOPIC_RESISTANCE_ACK) { topic, message ->
                logger.info("Message reçu sur {}: {}", topic, String(message.payload))
                resistanceStateService.updateResistanceStateFromEsp32Ack()
            }
        }
    }

    fun handleTopicTemperature(topic: String, message: MqttMessage, readingDeviceName: ReadingDeviceName) {
        logger.info("Lecture du message: {} reçu sur le topic: {}", message, topic)

        val stringValue = String(message.payload, Charsets.UTF_8);

        val sensorValue =
            stringValue.toDoubleOrNull() ?: throw IotSolarException.sensorValueIsNotADoubleValue(stringValue)

        temperatureService.createTemperature(sensorValue, readingDeviceName)
        logger.info("Température créée avec succès depuis le topic: {}", topic)
    }
}