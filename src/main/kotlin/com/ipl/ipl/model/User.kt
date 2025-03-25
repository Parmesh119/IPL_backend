package com.ipl.ipl.model

import java.util.*

data class User (
    val id: String? = UUID.randomUUID().toString(),
    val username: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class AuthResponse (
    val accessToken: String,
)