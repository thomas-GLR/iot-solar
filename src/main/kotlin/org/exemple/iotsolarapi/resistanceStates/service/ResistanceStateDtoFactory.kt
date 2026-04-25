package org.exemple.iotsolarapi.resistanceStates.service

import org.exemple.iotsolarapi.resistanceStates.dao.model.ResistanceState
import org.exemple.iotsolarapi.resistanceStates.interfaces.dto.ResistanceStateDto
import org.springframework.stereotype.Service

@Service
class ResistanceStateDtoFactory {

    fun resistanceStateDto(resistanceState: ResistanceState): ResistanceStateDto {
        return ResistanceStateDto(
            resistanceState.id,
            resistanceState.lastUpdate,
            resistanceState.currentState,
            resistanceState.requestedState
        )
    }

    fun resistanceStatesDtos(resistanceStates: List<ResistanceState>): List<ResistanceStateDto> {
        return resistanceStates.map { resistanceStateDto(it) }
    }
}