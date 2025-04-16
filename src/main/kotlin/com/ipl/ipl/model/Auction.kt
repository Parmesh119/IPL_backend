package com.ipl.ipl.model

data class Auction (
    val playerId: String,
    val name: String,
    val image_url: String? = "https://static-00.iconduck.com/assets.00/profile-circle-icon-512x512-zxne30hp.png",
    val basePrice: Double,
    val status: String,
    val country: String,
    val role: String,
    val iplTeam: String,
    var sellPrice: Double? = 0.0,
    val teamId: String? = null,
)