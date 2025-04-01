package com.ipl.ipl.model

import java.util.*

data class Team (
    val id: String? = UUID.randomUUID().toString(),
    val name: String,
    val owner: String,
    val coach: String,
    val captain: String,
    val viceCaptain: String,
    val players: Int = 0,
    val spent: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)