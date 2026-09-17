package org.exemple.iotsolarapi.authentication.interfaces

import org.exemple.iotsolarapi.authentication.interfaces.dto.AuthResponse
import org.exemple.iotsolarapi.authentication.interfaces.dto.LoginRequest
import org.exemple.iotsolarapi.authentication.interfaces.dto.RefreshTokenRequest
import org.exemple.iotsolarapi.authentication.interfaces.dto.RegisterRequest
import org.exemple.iotsolarapi.authentication.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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