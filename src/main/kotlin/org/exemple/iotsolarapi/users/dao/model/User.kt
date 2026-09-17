package org.exemple.iotsolarapi.users.dao.model

import jakarta.persistence.*
import org.exemple.iotsolarapi.roles.dao.model.Role
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
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
    var id: Long? = null,

    @Column(nullable = false)
    private var username: String,

    @Column(nullable = false)
    private var password: String,

    var refreshToken: String?,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    val roles: MutableSet<Role> = HashSet()
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        roles.map { SimpleGrantedAuthority(it.name.name) }

    override fun getPassword(): String? {
        return password
    }

    fun setPassword(password: String) {
        this.password = password
    }

    override fun getUsername(): String? {
        return username
    }

    fun addRole(role: Role) {
        this.roles.add(role)
    }

    fun clearRoles() {
        this.roles.clear()
    }
}