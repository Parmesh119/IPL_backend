package com.ipl.ipl.model

import java.time.Instant
import java.util.*

data class Player(
    val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val country: String,
    val age: Int?,
    val role: String,
    val battingStyle: String,
    val bowlingStyle: String?,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
)