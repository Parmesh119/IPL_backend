package com.ipl.ipl.model

import java.util.*

data class Team (
    val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val owner: String,
    val coach: String,
    val captain: String? = null,
    val viceCaptain: String? = null,
    val players: Int = 0,
    val spent: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Team_DTO (
    val id: String,
    val name: String,
    val owner: String,
    val coach: String,
    val captain: String? = "",
    val viceCaptain: String? = "",
    val players: Int? = 0,
    val spent: Double? = 0.0,
    val batsmenCount: Int? = 0,
    val bowlersCount: Int? = 0,
    val allRoundersCount: Int? = 0,
    val createdAt: Long,
    val updatedAt: Long,
    val playersBought: List<Player_DTO>? = emptyList()
)

data class Player_DTO (
    val srNo: Int = 0,
    val player: String = "",
    val iplTeam: String = "",
    val role: String = "",
    val price: Double = 0.0
)