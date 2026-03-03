package org.exemple.iotsolarapi.resistanceStates.interfaces.dto

import java.time.LocalDateTime

data class ResistanceStateDto(
    val id: Long?,
    val lastUpdate: LocalDateTime,
    val currentState: Boolean?,
    val requestedState: Boolean,
)