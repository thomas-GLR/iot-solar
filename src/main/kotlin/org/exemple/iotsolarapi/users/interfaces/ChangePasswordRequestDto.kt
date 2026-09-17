package org.exemple.iotsolarapi.users.interfaces

data class ChangePasswordRequestDto(
	val currentPassword: String,
	val newPassword: String
)
