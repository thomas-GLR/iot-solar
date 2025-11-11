package org.exemple.iotsolarapi.parameters.dao.repository

import org.exemple.iotsolarapi.parameters.dao.model.Parameter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ParameterRepository: JpaRepository<Parameter, Long> {
    fun findByName(name: String): Optional<Parameter>

    fun findAllByNameStartingWithIgnoreCase(name: String): List<Parameter>

    fun findByNameIn(names: Collection<String>): List<Parameter>
}