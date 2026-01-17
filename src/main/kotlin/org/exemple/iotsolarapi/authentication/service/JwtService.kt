package org.exemple.iotsolarapi.authentication.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.util.Date
import javax.crypto.SecretKey


@Service
class JwtService {
    @Value("\${security.jwt.secret-key}")
    private val secretKey: String? = null

    @Value("\${security.jwt.expiration-time}")
    private val jwtExpiration: Long = 0

    @Value("\${security.jwt.refresh-expiration-time}")
    private val refreshExpiration: Long = 0

    fun generateToken(username: String): String {
        username
        return createToken(username, jwtExpiration)
    }

    fun generateRefreshToken(username: String): String {
        return createToken(username, refreshExpiration)
    }

    private fun createToken(username: String, expiration: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + expiration)

        return Jwts.builder()
            .claims()
            .issuedAt(now)
            .expiration(expiryDate)
            .subject(username)
            .and()
            .signWith(getSignKey())
            .compact()
    }

    private fun getSignKey(): SecretKey {
        val keyBytes: ByteArray? = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)!!
    }

    fun extractExpiration(token: String): Date? {
        return extractClaim(token, Claims::getExpiration)
    }

    fun <T> extractClaim(token: String, claimsResolver: (Claims) -> T): T {
        val claims = extractAllClaims(token)
        return claimsResolver(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token)?.before(Date()) ?: true
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return (username == userDetails.username && !isTokenExpired(token))
    }

    fun validateToken(token: String): Boolean {
        return try {
            !isTokenExpired(token)
        } catch (e: Exception) {
            false
        }
    }
}