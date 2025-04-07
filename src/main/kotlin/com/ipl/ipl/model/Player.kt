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
    val basePrice: String,
    val sellPrice: String? = null,
    val iplTeam: String,
    var status: String? = "Pending",
    val createdAt: Long = Instant.now().toEpochMilli(),
    val updatedAt: Long = Instant.now().toEpochMilli()
)

data class player_team (
    val playerId: String,
    val teamId: String
)

data class PlayerList (
    val search: String? = null,
    val page: Int = 1,
    val size: Int = 10,
    val roles: List<String>? = null,
    val status: List<String>? = null,
    val iplTeam: List<String>? = null,
    val team: List<String>? = null,
)
