package org.exemple.iotsolarapi.users.service

import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.stream.Collectors


@Service
class IotSolarUserDetailsService(
	val userRepository: UserRepository
) : UserDetailsService {

	override fun loadUserByUsername(username: String): UserDetails {
		val user = userRepository.findByUsername(username)
			.orElseThrow {
				UsernameNotFoundException("User not found")
			}

		val authorities: MutableSet<GrantedAuthority?>? = user.roles.stream()
			.map { role -> SimpleGrantedAuthority(role.name.name) }
			.collect(Collectors.toSet())

		return User(
			user.username,
			user.password,
			authorities
		)
	}
}