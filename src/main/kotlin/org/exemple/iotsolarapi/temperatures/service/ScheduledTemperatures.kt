package org.exemple.iotsolarapi.temperatures.service

import jakarta.transaction.Transactional
import org.exemple.iotsolarapi.exception.IotSolarException.Companion.readingDeviceNameNotExist
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.exemple.iotsolarapi.readingDevices.dao.repository.ReadingDeviceRepository
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.exemple.iotsolarapi.temperatures.dao.repository.TemperatureRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Component
class ScheduledTemperatures(
    private val temperatureRepository: TemperatureRepository,
    private val readingDeviceRepository: ReadingDeviceRepository,
) {

    private val log: Logger = LoggerFactory.getLogger(ScheduledTemperatures::class.java)

    @Scheduled(fixedRate = 60 * 60 * 1000)
    @Transactional
    fun aggregateTemperatures() {
        log.info("Début de l'agrégation des températures")

        val today: LocalDate = LocalDate.now()

        val startOfDay: LocalDateTime = today.atStartOfDay()
        val endOfDay: LocalDateTime = today.atTime(LocalTime.MAX)

        val temperaturesOfDay = temperatureRepository.findByCollectionDateBetween(startOfDay, endOfDay)

        log.info("${temperaturesOfDay.size} températures de la journée remontées")

        val currentTime: LocalTime = LocalTime.now()
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val temperaturesOfDayWithoutCurrentHour = temperaturesOfDay.filter {
            it.collectionDate.toLocalTime() < currentTime
        }

        log.info("${temperaturesOfDayWithoutCurrentHour.size} températures de la journée remontées avec borne max heure précédente")

        if (temperaturesOfDayWithoutCurrentHour.isNotEmpty()) {
            val temperaturesByHour: Map<LocalTime, List<Temperature>> = temperaturesOfDayWithoutCurrentHour.groupBy {
                it.collectionDate.toLocalTime()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
            }

            val temperaturesToDelete = mutableListOf<Temperature>()
            val temperaturesToCreate = mutableListOf<Temperature>()

            val readingDevices = readingDeviceRepository.findAll()

            val readingDeviceTop = readingDevices
                .find { it.name == ReadingDeviceName.TOP }
                ?: run {
                    throw readingDeviceNameNotExist(ReadingDeviceName.TOP)
                }
            val readingDeviceMiddle = readingDevices
                .find { it.name == ReadingDeviceName.MIDDLE }
                ?: run {
                    throw readingDeviceNameNotExist(ReadingDeviceName.MIDDLE)
                }
            val readingDeviceBottom = readingDevices
                .find { it.name == ReadingDeviceName.BOTTOM }
                ?: run {
                    throw readingDeviceNameNotExist(ReadingDeviceName.BOTTOM)
                }

            temperaturesByHour
                .filter { it.value.size > 1 }
                .forEach { entry ->
                    val temperatures = entry.value
                    val currentTime = entry.key

                    val temperaturesTop = temperatures.filter { it.readingDeviceName() == ReadingDeviceName.TOP }
                    val temperaturesBottom = temperatures.filter { it.readingDeviceName() == ReadingDeviceName.BOTTOM }
                    val temperaturesMiddle = temperatures.filter { it.readingDeviceName() == ReadingDeviceName.MIDDLE }

                    if (temperaturesTop.isNotEmpty()) {
                        val aggregateTemperatureTop = Temperature(
                            null,
                            temperaturesTop.sumOf { it.value } / temperaturesTop.size,
                            LocalDateTime.of(today, currentTime),
                            readingDeviceTop)

                        temperaturesToCreate.add(aggregateTemperatureTop)
                    }

                    if (temperaturesMiddle.isNotEmpty()) {
                        val aggregateTemperatureMiddle = Temperature(
                            null,
                            temperaturesMiddle.sumOf { it.value } / temperaturesMiddle.size,
                            LocalDateTime.of(today, currentTime),
                            readingDeviceMiddle
                        )

                        temperaturesToCreate.add(aggregateTemperatureMiddle)
                    }

                    if (temperaturesBottom.isNotEmpty()) {
                        val aggregateTemperatureBottom = Temperature(
                            null,
                            temperaturesBottom.sumOf { it.value } / temperaturesBottom.size,
                            LocalDateTime.of(today, currentTime),
                            readingDeviceBottom
                        )

                        temperaturesToCreate.add(aggregateTemperatureBottom)
                    }
                    temperaturesToDelete.addAll(temperatures)
                }

            log.info("Supression de ${temperaturesToDelete.size} températures")
            temperatureRepository.deleteAll(temperaturesToDelete)

            temperatureRepository.flush()

            log.info("Création de ${temperaturesToCreate.size} températures")
            temperatureRepository.saveAll(temperaturesToCreate)
        } else {
            log.info("Aucunes températures à aggréger")
        }
    }
}