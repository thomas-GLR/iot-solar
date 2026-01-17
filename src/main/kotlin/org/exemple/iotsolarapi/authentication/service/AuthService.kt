package org.exemple.iotsolarapi.authentication.service

import org.exemple.iotsolarapi.authentication.interfaces.dto.AuthResponse
import org.exemple.iotsolarapi.users.dao.model.User
import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder) {

    fun login(username: String, password: String): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                username,
                password
            )
        )

        val userDetails = userDetailsService.loadUserByUsername(username)
        val token = jwtService.generateToken(userDetails.username)
        val refreshToken = jwtService.generateRefreshToken(userDetails.username)

        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            username = userDetails.username
        )
    }

    fun register(username: String, password: String): AuthResponse {
        val user = userRepository.findByUsername(username)

        if (user.isPresent) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "L'utilisateur existe déjà"
            )
        }

        val encodedPassword = passwordEncoder.encode(password)
        val newUser = User(
            username = username,
            password = encodedPassword,
            refreshToken = null
        )

        userRepository.save(newUser)

        val userDetails = userDetailsService.loadUserByUsername(newUser.username!!)
        val token = jwtService.generateToken(userDetails.username)
        val refreshToken = jwtService.generateRefreshToken(userDetails.username)

        return AuthResponse(
            token = token,
            refreshToken = refreshToken,
            username = userDetails.username
        )
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        val username = jwtService.extractUsername(refreshToken)
        val user = userDetailsService.loadUserByUsername(username)

        if (!jwtService.validateToken(refreshToken, user)) {
            throw IllegalArgumentException("Invalid refresh token")
        }

        val newAccessToken = jwtService.generateToken(user.username)
        val newRefreshToken = jwtService.generateRefreshToken(user.username)

        return AuthResponse(
            token = newAccessToken,
            refreshToken = newRefreshToken,
            username = username
        )
    }
}