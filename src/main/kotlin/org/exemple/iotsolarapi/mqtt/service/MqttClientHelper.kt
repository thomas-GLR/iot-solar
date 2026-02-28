package org.exemple.iotsolarapi.mqtt.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
class MqttClientHelper(
    private val mqttClient: IMqttClient,
) {
    private val logger: Logger = LoggerFactory.getLogger(MqttClientHelper::class.java)

    fun publish(topic: String, payload: String, qos: Int = 0, retained: Boolean) {
        val mqttMessage = MqttMessage()
        mqttMessage.payload = payload.toByteArray(StandardCharsets.UTF_8)
        mqttMessage.qos = qos
        mqttMessage.isRetained = retained
        try {
            mqttClient.publish(topic, mqttMessage)
        } catch (mqttException: MqttException) {
            logger.error(
                "Une erreur est survenue lors de la publication du message sur le topic {} : {}",
                topic,
                mqttException.message
            )
        }
    }

    /**
     * Envoie un message de manière suspendue.
     * @return Result.success(true) si le serveur a acquitté le message (QoS respecté),
     * Result.failure (exception) sinon.
     */
    suspend fun publishSuspend(topic: String, payload: String, qos: Int, retained: Boolean): Result<Unit> {
        // On bascule sur le contexte IO pour ne pas bloquer le thread appelant
        // pendant l'attente de l'acquittement réseau (surtout en QoS 2).
        return withContext(Dispatchers.IO) {
            val mqttMessage = MqttMessage()
            mqttMessage.payload = payload.toByteArray(StandardCharsets.UTF_8)
            mqttMessage.qos = qos
            mqttMessage.isRetained = retained

            try {
                // Avec IMqttClient, cette ligne BLOQUE jusqu'à ce que le message soit délivré
                // (et confirmé par le handshake QoS 2).
                mqttClient.publish(topic, mqttMessage)

                logger.info("Message publié avec succès sur le topic {}", topic)
                Result.success(Unit)
            } catch (mqttException: MqttException) {
                logger.error(
                    "Erreur lors de la publication sur {} : {}",
                    topic,
                    mqttException.message
                )
                Result.failure(mqttException)
            } catch (e: Exception) {
                logger.error("Erreur inattendue : {}", e.message)
                Result.failure(e)
            }
        }
    }
}