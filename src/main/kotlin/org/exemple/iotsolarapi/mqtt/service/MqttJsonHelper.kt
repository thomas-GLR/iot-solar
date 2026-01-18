package org.exemple.iotsolarapi.mqtt.service

import com.fasterxml.jackson.core.JacksonException
import com.fasterxml.jackson.databind.ObjectMapper
import org.exemple.iotsolarapi.exception.IotSolarException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MqttJsonHelper {
    private val objectMapper = ObjectMapper()

    private val logger: Logger = LoggerFactory.getLogger(MqttJsonHelper::class.java)

    fun <T> isValideJson(jsonString: String?, targetClass: Class<T>): Boolean {
        return convertJsonToObject(jsonString, targetClass) != null
    }

    fun <T> convertJsonToObject(jsonString: String?, targetClass: Class<T>): T?  {
        return try {
            objectMapper.readValue(jsonString, targetClass)
        } catch (_: JacksonException) {
            null
        }
    }

    fun <T> convertJsonToObjectOrThrow(jsonString: String, targetClass: Class<T>): T {
        return try {
            objectMapper.readValue(jsonString, targetClass)
        } catch (e: JacksonException) {
            logger.error("Erreur de conversion JSON vers {}: {}", targetClass.simpleName, e.message)
            throw IotSolarException.cantConvertJsonToObject(jsonString, targetClass.name)
        }
    }
}