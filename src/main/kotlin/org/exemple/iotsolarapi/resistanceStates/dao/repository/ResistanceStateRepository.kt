package org.exemple.iotsolarapi.resistanceStates.dao.repository

import org.exemple.iotsolarapi.resistanceStates.dao.model.ResistanceState
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ResistanceStateRepository : JpaRepository<ResistanceState, Long> {
    fun findTopByOrderByLastUpdateDesc(): Optional<ResistanceState>
}