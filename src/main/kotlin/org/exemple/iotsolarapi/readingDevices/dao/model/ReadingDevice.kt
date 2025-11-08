package org.exemple.iotsolarapi.readingDevices.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "reading_device")
@SequenceGenerator(
    name = "reading_device_seq",
    sequenceName = "reading_device_id_seq",
    allocationSize = 50
)
class ReadingDevice(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reading_device_seq")
    @Column(name = "id", nullable = false)
    private var id: Long? = null,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var name: ReadingDeviceName,
)