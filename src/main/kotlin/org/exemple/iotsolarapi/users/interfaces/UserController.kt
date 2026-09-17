package org.exemple.iotsolarapi.users.interfaces

import org.exemple.iotsolarapi.users.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me/password")
    fun updatePassword(
        @AuthenticationPrincipal currentUser: UserDetails,
        @RequestBody changePasswordRequestDto: ChangePasswordRequestDto
    ): ResponseEntity<String?> {
        return userService.updatePassword(currentUser, changePasswordRequestDto)
    }
}