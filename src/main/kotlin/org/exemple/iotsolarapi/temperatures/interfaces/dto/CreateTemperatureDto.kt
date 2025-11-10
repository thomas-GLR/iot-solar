package org.exemple.iotsolarapi.temperatures.interfaces.dto

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName

data class CreateTemperatureDto(val value: Double, val sensorName: ReadingDeviceName)
