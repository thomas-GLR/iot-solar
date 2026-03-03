package org.exemple.iotsolarapi.resistanceStates.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "resistance_state")
@SequenceGenerator(name = "resistance_state_seq", sequenceName = "resistance_state_id_seq", allocationSize = 50)
class ResistanceState(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "resistance_state_seq")
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    @Column(name = "current_state", nullable = true)
    var currentState: Boolean?,
    @Column(name = "requested_state", nullable = false)
    var requestedState: Boolean,
    @Column(name = "last_update", nullable = false)
    var lastUpdate: LocalDateTime,
)