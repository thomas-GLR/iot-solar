package org.exemple.iotsolarapi.admin.service

import org.exemple.iotsolarapi.users.dao.model.User
import org.exemple.iotsolarapi.users.dao.repository.UserRepository
import org.exemple.iotsolarapi.roles.dao.model.RoleName
import org.exemple.iotsolarapi.roles.dao.repository.RoleRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder


@Configuration
class AdminSeeder(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
) {
    @Value("\${security.admin.initial-password}")
    private val adminInitialPassword: String? = null

    @Bean
    fun initAdmin(passwordEncoder: PasswordEncoder): CommandLineRunner {
        return CommandLineRunner { args: Array<String?>? ->
            if (userRepository.findByUsername("admin").isEmpty) {
                val roleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow {
                    Exception("Role ${RoleName.ROLE_ADMIN.name} not found")
                }

                val admin = User(
                    null,
                    "admin",
                    passwordEncoder.encode(adminInitialPassword),
                    null,
                    HashSet()
                )

                admin.addRole(roleAdmin)

                userRepository.save<User>(admin)

            }
        }
    }
}