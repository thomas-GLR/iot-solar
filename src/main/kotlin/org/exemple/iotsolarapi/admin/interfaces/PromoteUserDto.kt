package org.exemple.iotsolarapi.admin.interfaces

import org.exemple.iotsolarapi.roles.dao.model.RoleName

data class PromoteUserDto(
	val newRoleName: RoleName
)
