package org.exemple.iotsolarapi.temperatures.service

import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.springframework.stereotype.Service

@Service
class TemperatureDtoFactory {
    fun temperaturesDto(temperatures: List<Temperature>): List<TemperatureDto> {
        return temperatures.map { temperature ->
            TemperatureDto(
                temperature.id,
                temperature.value,
                temperature.collectionDate,
                temperature.readingDeviceName()
            )
        }
    }
}