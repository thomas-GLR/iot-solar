package org.exemple.iotsolarapi.temperatures.dao.repository

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TemperatureRepository : JpaRepository<Temperature, Long>, TemperatureRepositoryCriteria {
    fun findByCollectionDateBetween(
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Temperature>

    fun findByReadingDeviceAndCollectionDateBetweenOrderByCollectionDateDesc(
        readingDevice: ReadingDevice,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<Temperature>

    @Query("""
        SELECT t.* FROM temperatures t
        INNER JOIN (
            SELECT reading_device_id, MAX(collection_date) as max_date
            FROM temperatures
            GROUP BY reading_device_id
        ) latest ON t.reading_device_id = latest.reading_device_id 
                 AND t.collection_date = latest.max_date
    """, nativeQuery = true)
    fun findLatestTemperaturePerDevice(): List<Temperature>
}