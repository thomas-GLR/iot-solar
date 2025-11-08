package org.exemple.iotsolarapi.authentication.interfaces

import org.exemple.iotsolarapi.authentication.interfaces.dto.AuthResponse
import org.exemple.iotsolarapi.authentication.interfaces.dto.LoginRequest
import org.exemple.iotsolarapi.authentication.interfaces.dto.RegisterRequest
import org.exemple.iotsolarapi.authentication.service.JwtService
import org.exemple.iotsolarapi.exception.IotSolarException
import org.exemple.iotsolarapi.users.dao.model.User
import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): AuthResponse {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(
                loginRequest.username,
                loginRequest.password
            )
        )

        val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
        val token = jwtService.generateToken(userDetails.username)

        return AuthResponse(
            token = token,
            username = userDetails.username
        )
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<AuthResponse> {
        val user = userRepository.findByUsername(registerRequest.username)

        if (user.isPresent) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "L'utilisateur existe déjà"
            )
        }

        val encodedPassword = passwordEncoder.encode(registerRequest.password)
        val newUser = User(
            username = registerRequest.username,
            password = encodedPassword,
            refreshToken = null
        )

        userRepository.save(newUser)

        val userDetails = userDetailsService.loadUserByUsername(newUser.username!!)
        val token = jwtService.generateToken(userDetails.username)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(
                AuthResponse(
                    token = token,
                    username = userDetails.username
                )
            )
    }
}