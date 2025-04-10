package com.ipl.ipl.model

data class Auction (
    val playerId: String,
    val name: String,
    val basePrice: Double,
    val status: String,
    val country: String,
    val role: String,
    val iplTeam: String,
    var sellPrice: Double? = 0.0,
    val teamId: String? = null,
)