package org.exemple.iotsolarapi.temperatures.interfaces.dto

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import java.time.LocalDateTime

data class TemperatureDto(
    val id: Long?,
    val value: Double,
    val collectionDate: LocalDateTime,
    val readingDeviceName: ReadingDeviceName
)
