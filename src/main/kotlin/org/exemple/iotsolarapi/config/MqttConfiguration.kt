package org.exemple.iotsolarapi.config

import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MqttConfiguration {

    @Value("\${mqtt.broker.address:localhost}")
    private val brokerAddress: String? = null

    @Value("\${mqtt.broker.hasSSL:false}")
    private val hasSSL: Boolean? = null

    @Value("\${mqtt.broker.port:1883}")
    private val port = 1883

    @Value("\${mqtt.broker.userName:}")
    private val userName: String? = null

    @Value("\${mqtt.broker.password:}")
    private val password: String? = null

    private val TCP = "tcp://"
    private val SSL = "ssl://"
    private val clientId: String? = MqttClient.generateClientId()
    private val colon = ":"

    private val logger: Logger = LoggerFactory.getLogger(MqttConfiguration::class.java)

    @Bean
    fun mqttClient(): IMqttClient? {
        logger.info("Création du client MQTT à l'adresse {}:{} avec l'utilisateur {}", brokerAddress, port, userName)

        val connectionType = if (this.hasSSL != null && this.hasSSL) this.SSL else this.TCP

        val brokerUrl = connectionType + this.brokerAddress + colon + this.port
        val connectionOptions = MqttConnectOptions()

        try {
            val mqttClient: IMqttClient = MqttClient(brokerUrl, clientId, MemoryPersistence())

            connectionOptions.isCleanSession = true
            connectionOptions.password = this.password!!.toCharArray()
            connectionOptions.userName = this.userName

            mqttClient.connect(connectionOptions)

            return mqttClient
        } catch (me: MqttException) {
            logger.error("Error connecting to MQTT : {}", me.message, me)
        }

        return null
    }
}