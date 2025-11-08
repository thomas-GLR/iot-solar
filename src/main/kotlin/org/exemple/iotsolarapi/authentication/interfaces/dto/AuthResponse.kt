package org.exemple.iotsolarapi.authentication.interfaces.dto

data class AuthResponse(
    val token: String,
    val type: String = "Bearer",
    val username: String
)
