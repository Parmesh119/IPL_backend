package com.ipl.ipl.model

data class Auction (
    val playerId: String,
    val name: String,
    val basePrice: String,
    val status: String,
    val country: String,
    val role: String,
    val iplTeam: String,
    var sellPrice: String? = null,
    val teamId: String? = null,
)