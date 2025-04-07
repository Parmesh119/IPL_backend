package com.ipl.ipl.Repository

import com.ipl.ipl.config.JwtUtil
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.RegisterRequest
import com.ipl.ipl.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class UserRepository(
    private val jdbcTemplate: JdbcTemplate,
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    private val rowMapper = { rs: ResultSet, _: Int ->
        User(
            id = rs.getString("id"),
            username = rs.getString("username"),
            password = rs.getString("password"),
            role = rs.getString("role"),
            createdAt = rs.getLong("created_at")
        )
    }

    fun login(username: String, password: String): AuthResponse {
        try {
            // Fetch the stored password from the database
            val storedUser: User? = try {
                jdbcTemplate.queryForObject(
                    "SELECT * FROM users WHERE username = ?",
                    rowMapper,
                    username
                )
            } catch (e: Exception) {
                throw Exception("Failed to fetch password from the database")
            }

            // Validate username and password (plain text comparison)
            if (storedUser?.password != password) {
                throw Exception("Invalid username or password")
            }

            // Generate JWT token if authentication is successful
            val accessToken = jwtUtil.generateAcessToken(username, storedUser.id!!, storedUser.role.split(","))
            val refreshToken = jwtUtil.generateRefreshToken(username)

            // Store the refresh token in the database
            val refreshTokenId = UUID.randomUUID().toString()
            val userId = storedUser.id
            val expirationTime: Long = System.currentTimeMillis() + 604800000
            val createdAt: Long = System.currentTimeMillis()
            refreshTokenRepository.saveRefreshToken(refreshTokenId, userId, refreshToken, createdAt, expirationTime)

            return AuthResponse(accessToken = accessToken, refreshToken = refreshToken)
        } catch (e: Exception) {
            throw Exception("Failed to login")
        }
    }

    fun register(registerRequest: RegisterRequest): User {
        try {
            // Check if the username already exists
            val existingUser: User? = try {
                jdbcTemplate.query(
                    "SELECT * FROM users WHERE username = ?",
                    rowMapper,
                    registerRequest.username
                ).firstOrNull() // ✅ Use query() instead of queryForObject()
            } catch (e: Exception) {
                println(e.message)
                throw Exception("Failed to fetch user from the database")
            }

            if (existingUser != null) {
                throw Exception("Username already exists")
            }

            val newUserId = registerRequest.id ?: UUID.randomUUID().toString()

            // Insert the new user into the database
            jdbcTemplate.update(
                "INSERT INTO users (id, username, password, name, role, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                newUserId,
                registerRequest.username,
                registerRequest.password,
                registerRequest.name,
                registerRequest.role.joinToString(","),
                registerRequest.createdAt
            )

            return User(
                id = newUserId,
                username = registerRequest.username,
                password = registerRequest.password,
                role = registerRequest.role.joinToString(","),
                createdAt = registerRequest.createdAt
            )
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Failed to register user")
        }
    }

    fun getUserDetails(username: String): User {
        try {
            return jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE username = ?",
                rowMapper,
                username
            ) ?: throw Exception("User not found")
        } catch (e: Exception) {
            throw Exception("Failed to get user details")
        }
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        try {
            // Validate the refresh token
            val username = jwtUtil.getSubjectFromToken(refreshToken)
            val user = getUserDetails(username)
            // Generate a new access token
            val newAccessToken = jwtUtil.generateAcessToken(username, user.id!!, user.role.split(","))
            val newRefreshToken = jwtUtil.generateRefreshToken(username)

            // Store the new refresh token in the database
            val getRefreshTokenId = refreshTokenRepository.getRefreshToken(refreshToken)
            refreshTokenRepository.updateRefreshToken(newRefreshToken, getRefreshTokenId?.id!!)

            return AuthResponse(accessToken = newAccessToken, refreshToken = newRefreshToken)
        } catch (e: Exception) {
            throw Exception("Failed to refresh token")
        }
    }
}