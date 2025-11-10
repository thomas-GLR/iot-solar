package org.exemple.iotsolarapi.temperatures.dao.repository

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Predicate
import org.exemple.iotsolarapi.temperatures.dao.model.Temperature
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class TemperatureRepositoryCriteriaImpl : TemperatureRepositoryCriteria {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    override fun findTemperaturesOnPeriod(
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): List<Temperature> {
        val criteriaBuilder = entityManager.criteriaBuilder
        val criteriaQuery = criteriaBuilder.createQuery(Temperature::class.java)
        val root = criteriaQuery.from(Temperature::class.java)

        val predicates = mutableListOf<Predicate>()

        if (startDate != null && endDate != null) {
            predicates.add(criteriaBuilder.between(root.get("collectionDate"), startDate, endDate))
        }

        criteriaQuery.where(*predicates.toTypedArray())

        return entityManager.createQuery(criteriaQuery).resultList
    }
}