package org.exemple.iotsolarapi.roles.dao.repository

import org.exemple.iotsolarapi.roles.dao.model.Role
import org.exemple.iotsolarapi.roles.dao.model.RoleName
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
	fun findByName(name: RoleName): Optional<Role>
}