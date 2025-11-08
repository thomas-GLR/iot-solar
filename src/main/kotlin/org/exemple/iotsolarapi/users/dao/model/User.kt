package org.exemple.iotsolarapi.users.dao.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Table(name = "users")
@SequenceGenerator(
    name = "users_seq",
    sequenceName = "users_id_seq",
    allocationSize = 50
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Column(name = "id", nullable = false)
    private var id: Long? = null,

    @Column(nullable = false)
    private var username: String,

    @Column(nullable = false)
    private var password: String,

    private var refreshToken: String?
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return null
    }

    override fun getPassword(): String? {
        return password
    }

    override fun getUsername(): String? {
        return username
    }
}