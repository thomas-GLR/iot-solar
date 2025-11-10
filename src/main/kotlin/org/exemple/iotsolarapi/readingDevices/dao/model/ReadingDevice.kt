package org.exemple.iotsolarapi.readingDevices.dao.model

import jakarta.persistence.*
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature

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
    var id: Long? = null,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var name: ReadingDeviceName,
    @OneToMany(mappedBy = "readingDevice", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    var temperatures: MutableSet<Temperature> = mutableSetOf(),
)