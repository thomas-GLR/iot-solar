package org.exemple.iotsolarapi.temperatures.service

import jakarta.transaction.Transactional
import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.mqtt.service.MqttService
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.exemple.iotsolarapi.readingDevices.dao.repository.ReadingDeviceRepository
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.exemple.iotsolarapi.temperatures.dao.repository.TemperatureRepository
import org.exemple.iotsolarapi.temperatures.interfaces.dto.AggregationType
import org.exemple.iotsolarapi.temperatures.interfaces.dto.CreateTemperatureDto
import org.exemple.iotsolarapi.temperatures.interfaces.dto.TemperatureDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val logger: Logger = LoggerFactory.getLogger(TemperatureService::class.java)

    fun getAllTemperaturesForStartDateAndEndDate(
        aggregationType: AggregationType?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<TemperatureDto> {
        val temperatures = if (startDate != null && endDate != null) {
            temperatureRepository.findByCollectionDateBetween(startDate, endDate)
        } else {
            temperatureRepository.findAll()
        }

        val temperaturesDto = mutableListOf<TemperatureDto>()

        if (aggregationType != null) {
            val temperaturesByDateByReadingDevice =
                HashMap<ReadingDevice, HashMap<LocalDateTime, MutableList<Temperature>>>()

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

            temperaturesByDateByReadingDevice.forEach { entryMapByReadingDevice ->
                val readingDevice = entryMapByReadingDevice.key

                entryMapByReadingDevice.value.forEach { entryTemperaturesByDate ->
                    val date = entryTemperaturesByDate.key
                    val temperatures = entryTemperaturesByDate.value

                    val sum = temperatures.map { it.value }.average()

                    temperaturesDto.add(TemperatureDto(
                        null,
                        sum,
                        date,
                        readingDevice.name
                    ))
                }
            }
        } else {
            temperaturesDto.addAll(temperatureDtoFactory.temperaturesDto(temperatures))
        }

        temperaturesDto.sortByDescending { it.collectionDate }

        return temperaturesDto
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
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        readingDeviceName: ReadingDeviceName
    ): List<TemperatureDto> {
        val readingDevice = readingDeviceRepository.findByName(readingDeviceName).orElseThrow {
            IotSolarException.readingDeviceNameNotExist(readingDeviceName)
        }
        val temperatures = temperatureRepository.findByReadingDeviceAndCollectionDateBetweenOrderByCollectionDateDesc(
            readingDevice,
            startDate,
            endDate
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

    @Transactional
    fun createTemperature(sensorValue: Double, readingDeviceName: ReadingDeviceName) {
        val readingDevice = readingDeviceRepository.findByName(readingDeviceName).orElseThrow {
            IotSolarException.readingDeviceNameNotExist(readingDeviceName)
        }

        val temperature = Temperature(
            null,
            sensorValue,
            now(),
            readingDevice
        )

        logger.info("Création de la température pour la sonde {} avec la valeur {} à la date {}",
            readingDeviceName, sensorValue, temperature.collectionDate)

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