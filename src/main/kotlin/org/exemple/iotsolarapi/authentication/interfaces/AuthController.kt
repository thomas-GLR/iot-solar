package org.exemple.iotsolarapi.authentication.interfaces

import org.exemple.iotsolarapi.authentication.interfaces.dto.AuthResponse
import org.exemple.iotsolarapi.authentication.interfaces.dto.LoginRequest
import org.exemple.iotsolarapi.authentication.interfaces.dto.RefreshTokenRequest
import org.exemple.iotsolarapi.authentication.interfaces.dto.RegisterRequest
import org.exemple.iotsolarapi.authentication.service.AuthService
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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): AuthResponse {
        return authService.login(loginRequest.username, loginRequest.password)
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody registerRequest: RegisterRequest): AuthResponse {
        return authService.register(registerRequest.username, registerRequest.password)
    }

    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): AuthResponse {
        return authService.refreshToken(request.refreshToken)
    }
}