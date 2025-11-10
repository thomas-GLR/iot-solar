package org.exemple.iotsolarapi.temperatures.dao.repository

import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import java.time.LocalDate
import java.time.LocalDateTime

interface TemperatureRepositoryCriteria {
    fun findTemperaturesOnPeriod(startDate: LocalDateTime?, endDate: LocalDateTime?): List<Temperature>
}