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
    val refreshToken: String
)

data class RegisterRequest (
    val id: String? = UUID.randomUUID().toString(),
    val username: String,
    val name: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis(),
)

data class RefreshTokenRequest (
    val refreshToken: String
)