package org.exemple.iotsolarapi.readingDevices.dao.repository

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.springframework.data.jpa.repository.JpaRepository

interface ReadingDeviceRepository: JpaRepository<ReadingDevice, Long> {
}