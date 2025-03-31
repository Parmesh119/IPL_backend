package com.ipl.ipl.Repository

import com.ipl.ipl.model.RefreshToken
import com.ipl.ipl.model.RefreshTokenRequest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository (
    private val jdbcTemplate: JdbcTemplate
) {
    fun saveRefreshToken(id: String, userId: String, token: String, createdAt: Long, expiresAt: Long) {
        try {
            jdbcTemplate.update(
                "INSERT INTO refresh_tokens (id, user_id, token, created_at, expires_at) VALUES (?, ?, ?, ?, ?)",
                id,
                userId,
                token,
                createdAt,
                expiresAt
            )
        } catch (e: Exception) {
            println(e)
            e.printStackTrace()
            throw Exception("Failed to save refresh token")
        }
    }

    fun getRefreshToken(token: String): RefreshToken? {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM refresh_tokens WHERE token = ?",
                { rs, _ ->
                    RefreshToken(
                        id = rs.getString("id"),
                        userId = rs.getString("user_id"),
                        token = rs.getString("token"),
                        createdAt = rs.getLong("created_at"),
                        expiresAt = rs.getLong("expires_at")
                    )
                },
                token
            )
        } catch (e: Exception) {
            throw Exception("Failed to fetch refresh token from the database")
        }
    }

    fun deleteRefreshToken(token: String) {
        try {
            jdbcTemplate.update(
                "DELETE FROM refresh_tokens WHERE token = ?",
                token
            )
        } catch (e: Exception) {
            throw Exception("Failed to delete refresh token")
        }
    }

    fun updateRefreshToken(token: String, Id: String) {
        try {
            jdbcTemplate.update(
                "UPDATE refresh_tokens SET token = ? WHERE id = ?",
                token,
                Id
            )
        } catch (e: Exception) {
            throw Exception("Failed to update refresh token")
        }
    }
}