package org.exemple.iotsolarapi.admin.service

import org.exemple.iotsolarapi.admin.interfaces.PromoteUserDto
import org.exemple.iotsolarapi.roles.dao.repository.RoleRepository
import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdminService(
	private val userRepository: UserRepository,
	private val passwordEncoder: PasswordEncoder,
	private val roleRepository: RoleRepository,
) {
	fun resetPassword(id: Long) {
		val user = userRepository.findById(id).orElseThrow { Exception("User not found") }
		user.setPassword(passwordEncoder.encode(""))
		userRepository.save(user)
	}

	fun promote(id: Long, promoteUserDto: PromoteUserDto) {
		val user = userRepository.findById(id).orElseThrow { Exception("User not found") }

		val roles = roleRepository.findAll()

		val role = roles.find { role -> role.name == promoteUserDto.newRoleName }

		if (role == null) {
			throw Exception("Role ${promoteUserDto.newRoleName.name} not found")
		}

		user.addRole(role)

		userRepository.save(user)
	}

	fun clearRoles(id: Long) {
		val user = userRepository.findById(id).orElseThrow { Exception("User not found") }
		user.clearRoles()
		userRepository.save(user)
	}
}