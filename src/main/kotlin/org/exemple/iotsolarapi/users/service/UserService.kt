package org.exemple.iotsolarapi.users.service

import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.exemple.iotsolarapi.users.interfaces.ChangePasswordRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserService(
	val userRepository: UserRepository,
	val passwordEncoder: PasswordEncoder
) {

	fun updatePassword(
		currentUser: UserDetails,
		changePasswordRequestDto: ChangePasswordRequestDto
	): ResponseEntity<String?> {
		val user = userRepository.findByUsername(currentUser.username).orElseThrow {
			UsernameNotFoundException("Connected user not found")
		}

		if (!passwordEncoder.matches(changePasswordRequestDto.currentPassword, user.password)) {
			return ResponseEntity.status(403).body<String?>("Current password incorrect")
		}

		user.setPassword(passwordEncoder.encode(changePasswordRequestDto.newPassword))
		userRepository.save(user)

		return ResponseEntity.ok().build();
	}
}