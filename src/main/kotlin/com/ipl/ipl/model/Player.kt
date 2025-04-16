package com.ipl.ipl.model

import java.time.Instant
import java.util.*

data class Player(
    val id: String? = UUID.randomUUID().toString(),
    val image_url: String? = "https://static-00.iconduck.com/assets.00/profile-circle-icon-512x512-zxne30hp.png",
    val name: String,
    val country: String,
    val age: Int? = null,
    val role: String,
    val battingStyle: String? = null,
    val bowlingStyle: String? = null,
    var teamId: String? = null,
    val basePrice: Double,
    var sellPrice: Double? = 0.0,
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
