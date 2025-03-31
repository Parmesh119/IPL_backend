package com.ipl.ipl.model

import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.*

data class RefreshToken (
    @Id
    val id: String? = UUID.randomUUID().toString(),
    val userId: String,
    val token: String,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val expiresAt: Long = Instant.now().toEpochMilli() // 1 hour in milliseconds
)