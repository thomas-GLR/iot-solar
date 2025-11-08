package org.exemple.iotsolarapi.temperatures.service

import org.exemple.iotsolarapi.temperatures.interfaces.dto.AggregationType
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TemperatureService {
    fun getAllTemperaturesForStartDateAndEndDate(aggregationType: AggregationType?, startDate: LocalDateTime?, endDate: LocalDateTime?): List<TemperatureDto> {

    }
}