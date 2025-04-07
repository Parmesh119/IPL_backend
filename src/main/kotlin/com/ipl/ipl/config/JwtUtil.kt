package com.ipl.ipl.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var SECRET_KEY: String

    @Value("\${jwt.access.expiration}")
    private var EXPIRATION_TIME: Long = 0

    // Using a single key creation method for consistency
    private val key by lazy {
        SecretKeySpec(SECRET_KEY.toByteArray(), SignatureAlgorithm.HS256.jcaName)
    }

    fun generateAcessToken(
        subject: String,
        id: String,
        role: List<String>,
        claims: Map<String, Any>? = null,
        expirationMinutes: Long = EXPIRATION_TIME,
    ): String {
        try {
            val now = Instant.now()
            val expiration = now.plusSeconds(expirationMinutes * 60)

            val jwtBuilder = Jwts.builder()
                .setSubject(subject)
                .claim("userId", id)
                .claim("role", role)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key, SignatureAlgorithm.HS256)

            claims?.let { jwtBuilder.addClaims(it) }

            return jwtBuilder.compact()
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate JWT token", e)
        }
    }

    fun generateRefreshToken(username: String): String {
        return try {
            val now = Instant.now()
            val expiration = now.plusSeconds(60 * 60 * 24 * 7) // 7 days

            Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate refresh token", e)
        }
    }

    fun getSubjectFromToken(token: String): String {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(key) // Using the same key instance here
                .build()
                .parseClaimsJws(token)
                .body

            claims.subject ?: throw RuntimeException("Subject not found in token")
        } catch (e: JwtException) {
            throw RuntimeException("Invalid JWT token", e)
        }
    }

    fun extractClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        ?: throw RuntimeException("Claims not found in token")
    }

    fun extractUsername(token: String): String {
        return extractClaims(token).subject
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractClaims(token).expiration.before(Date())
    }

    fun extractRoles(token: String): List<String> {
        val claims = extractClaims(token)
        return claims["role"] as? List<String> ?: emptyList()
    }

    fun getUserIdFromToken(token: String): String {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY.toByteArray(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .body

            // Get the userId from the claims
            claims.get("userId", String::class.java) ?: throw Exception("User ID not found in token")
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Invalid token")
        }
    }

    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        return if (authHeader != null && authHeader.startsWith("Bearer ")) {
            authHeader.substring(7)
        } else null
    }
}