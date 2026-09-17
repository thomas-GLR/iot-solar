package org.exemple.iotsolarapi.security

import jakarta.servlet.DispatcherType
import org.exemple.iotsolarapi.authentication.service.JwtAuthenticationFilter
import org.exemple.iotsolarapi.users.service.IotSolarUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SpringSecurity(
	private val jwtAuthenticationFilter: JwtAuthenticationFilter,
	private val iotSolarUserDetailsService: IotSolarUserDetailsService
) {

	@Bean
	fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
		http
			.csrf { it.disable() }
			.authorizeHttpRequests { auth ->
				auth
					.dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
					.requestMatchers("/").permitAll()
					.requestMatchers("/auth/**").permitAll()
					.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/error").permitAll()
					.anyRequest().authenticated()
			}
			.logout {
				it.logoutUrl("/logout").permitAll()
			}
			.sessionManagement { session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			}
			.authenticationProvider(authenticationProvider())
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

		return http.build()
	}

	@Bean
	fun authenticationProvider(): AuthenticationProvider {
		val authProvider = DaoAuthenticationProvider(iotSolarUserDetailsService)
		authProvider.setPasswordEncoder(passwordEncoder())
		return authProvider
	}

	@Bean
	fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
		return config.authenticationManager
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder {
		return BCryptPasswordEncoder()
	}
}