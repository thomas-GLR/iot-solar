package org.exemple.iotsolarapi.admin.interfaces

import org.exemple.iotsolarapi.admin.service.AdminService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/admin")
class AdminController(private val adminService: AdminService) {

	@PostMapping("/users/{id}/reset-password")
	fun resetPassword(@PathVariable id: Long) {
		adminService.resetPassword(id)
	}

	@PostMapping("/users/{id}/promote")
	fun promote(@PathVariable id: Long, @RequestBody promoteUserDto: PromoteUserDto) {
		// req.newRole() validated against an allow-list, never blindly trusted
		adminService.promote(id, promoteUserDto)
	}

	@DeleteMapping("/users/{id}/clear-roles")
	fun clearRoles(@PathVariable id: Long) {
		adminService.clearRoles(id)
	}
}