package org.exemple.iotsolarapi.readingDevices.dao.repository

import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ReadingDeviceRepository: JpaRepository<ReadingDevice, Long> {
    fun findByName(name: ReadingDeviceName): Optional<ReadingDevice>
}