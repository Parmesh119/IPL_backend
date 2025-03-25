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
    val teamId: String? = null,
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
)

data class player_team (
    val playerId: String,
    val teamId: String
)