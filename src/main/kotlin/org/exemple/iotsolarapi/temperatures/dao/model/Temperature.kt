package org.exemple.iotsolarapi.temperatures.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDevice
import org.exemple.iotsolarapi.readingDevices.dao.model.ReadingDeviceName
import java.time.LocalDateTime

@Entity
@Table(name = "temperatures")
@SequenceGenerator(name = "temperatures_seq", sequenceName = "temperatures_id_seq", allocationSize = 50)
class Temperature(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "temperatures_seq")
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    @Column(nullable = false)
    var value: Double,
    @Column(nullable = false)
    var collectionDate: LocalDateTime,
    @ManyToOne
    @JoinColumn(name="reading_device_id", nullable=false)
    var readingDevice: ReadingDevice,
) {
    fun readingDeviceName(): ReadingDeviceName {
        return readingDevice.name
    }
}