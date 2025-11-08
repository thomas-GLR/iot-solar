package org.exemple.iotsolarapi.temperatures.dao.repository

import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TemperatureRepository : JpaRepository<Temperature, Long> {
}