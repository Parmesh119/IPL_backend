package com.ipl.ipl.config

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration
class JwtUtil {

    @Value("\${jwt.secret}")
    private lateinit var SECRET_KEY: String

    @Value("\${jwt.access.expiration}")
    private lateinit var EXPIRATION_TIME: String

    private val key by lazy {
        val hmacKey = SECRET_KEY.toByteArray()
        SecretKeySpec(hmacKey, SignatureAlgorithm.HS256.jcaName)
    }

    fun generateToken(
        subject: String,
        claims: Map<String, Any>? = null,
        expirationMinutes: Long = 60
    ): String {
        try {
            val now = Instant.now()
            val expiration = now.plusSeconds(expirationMinutes * 60)

            val jwtBuilder = Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(key, SignatureAlgorithm.HS256)

            claims?.let { jwtBuilder.addClaims(it) }

            return jwtBuilder.compact()
        } catch (e: Exception) {
            throw RuntimeException("Failed to generate JWT token", e)
        }
    }
}