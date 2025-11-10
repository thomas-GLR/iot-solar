package org.exemple.iotsolarapi.temperatures.service

import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.exemple.iotsolarapi.readingDevices.dao.repository.ReadingDeviceRepository
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.exemple.iotsolarapi.temperatures.dao.repository.TemperatureRepository
import org.exemple.iotsolarapi.temperatures.interfaces.dto.AggregationType
import org.exemple.iotsolarapi.temperatures.interfaces.dto.CreateTemperatureDto
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Service
class TemperatureService(
    val temperatureRepository: TemperatureRepository,
    val temperatureDtoFactory: TemperatureDtoFactory,
    val readingDeviceRepository: ReadingDeviceRepository
) {
    fun getAllTemperaturesForStartDateAndEndDate(
        aggregationType: AggregationType?,
        startDate: LocalDate?,
        endDate: LocalDate?
    ): List<TemperatureDto> {
        val temperatures = if (startDate != null && endDate != null) {
            temperatureRepository.findByCollectionDateBetween(startDate.atStartOfDay(), endDate.atStartOfDay())
        } else {
            temperatureRepository.findAll()
        }

        if (aggregationType != null) {
            val temperaturesByDateByReadingDevice = HashMap<ReadingDevice, HashMap<LocalDateTime, MutableList<Temperature>>>()

            temperatures.forEach { temperature ->
                val collectionDate = temperature.collectionDate
                val readingDevice = temperature.readingDevice

                val dateKey = when (aggregationType) {
                    AggregationType.DAYS -> LocalDateTime.of(
                        collectionDate.year,
                        collectionDate.monthValue,
                        collectionDate.dayOfMonth,
                        collectionDate.hour,
                        collectionDate.minute
                    )

                    AggregationType.HOURS -> LocalDateTime.of(
                        collectionDate.year,
                        collectionDate.monthValue,
                        collectionDate.dayOfMonth,
                        collectionDate.hour,
                        0
                    )

                    AggregationType.MONTHS -> LocalDateTime.of(
                        collectionDate.year,
                        collectionDate.monthValue,
                        collectionDate.dayOfMonth,
                        0,
                        0
                    )

                    AggregationType.YEARS -> LocalDateTime.of(
                        collectionDate.year,
                        collectionDate.monthValue,
                        1,
                        0,
                        0
                    )
                }

                temperaturesByDateByReadingDevice.putIfAbsent(readingDevice, HashMap())
                temperaturesByDateByReadingDevice[readingDevice]!!.putIfAbsent(dateKey, mutableListOf())
                temperaturesByDateByReadingDevice[readingDevice]?.get(dateKey)?.add(temperature)
            }

            temperaturesByDateByReadingDevice.keys.forEach { readingDevice ->

            }
        }

        return temperatureDtoFactory.temperaturesDto(temperatures)
    }

    /**
     * Get temperatures detail for a period and a specific reading device.
     *
     * @param startDate the start date of the period.
     * @param endDate the end date of the period.
     * @param readingDeviceName the name of the reading device
     * @return a list of temperatures for the table detail.
     */
    fun getTemperaturesDetail(
        startDate: LocalDate,
        endDate: LocalDate,
        readingDeviceName: ReadingDeviceName
    ): List<TemperatureDto> {
        val readingDevice = readingDeviceRepository.findByName(readingDeviceName).orElseThrow {
            IotSolarException.readingDeviceNameNotExist(readingDeviceName)
        }
        val temperatures = temperatureRepository.findByReadingDeviceAndCollectionDateBetweenOrderByCollectionDateDesc(
            readingDevice,
            startDate.atStartOfDay(),
            endDate.atStartOfDay()
        )

        return temperatureDtoFactory.temperaturesDto(temperatures)
    }

    /**
     * Create the new temperature.
     *
     * @param createTemperatureDto dto to create the new temperature.
     */
    fun createTemperature(createTemperatureDto: CreateTemperatureDto) {
        val readingDeviceName = createTemperatureDto.sensorName

        val readingDevice = readingDeviceRepository.findByName(readingDeviceName).orElseThrow {
            IotSolarException.readingDeviceNameNotExist(readingDeviceName)
        }

        val temperature = Temperature(
            null,
            createTemperatureDto.value,
            now(),
            readingDevice
        )

        temperatureRepository.save(temperature)
    }

    /**
     * Get last temperatures of all reading device.
     *
     * @return a list of last temperatures for each reading device.
     */
    fun getLastTemperatures(): List<TemperatureDto> {
        val temperatures = temperatureRepository.findLatestTemperaturePerDevice()

        return temperatureDtoFactory.temperaturesDto(temperatures)
    }
}