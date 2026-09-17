package org.exemple.iotsolarapi.roles.dao.model

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
@Table(name = "roles")
@SequenceGenerator(
    name = "roles_seq",
    sequenceName = "roles_id_seq",
    allocationSize = 1
)
class Role(roleName: RoleName) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "roles_seq")
    val id: Long? = null

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    val name: RoleName = roleName
}