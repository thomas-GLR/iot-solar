package org.exemple.iotsolarapi.parameters.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "parameters")
@SequenceGenerator(name = "parameters_seq", sequenceName = "parameters_id_seq", allocationSize = 50)
class Parameter(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "parameters_seq")
    @Column(name = "id", nullable = false)
    var id: Long? = null,
    @Column(nullable = false)
    var name: String,
    @Column(nullable = false)
    var value: String
)