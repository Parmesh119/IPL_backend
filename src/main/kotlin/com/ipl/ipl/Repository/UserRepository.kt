package com.ipl.ipl.Repository

import com.ipl.ipl.config.JwtUtil
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.User
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class UserRepository (
    private val jdbcTemplate: JdbcTemplate,
    private val jwtUtil: JwtUtil
) {

    private val rowMapper = { rs: java.sql.ResultSet, _: Int ->
        User(
            id = rs.getString("id"),
            username = rs.getString("username"),
            password = rs.getString("password"),
            createdAt = rs.getLong("created_at")
        )
    }

    fun login(username: String, password: String): AuthResponse {
        try {
            // Fetch the stored password from the database
            val storedPassword: String? = try {
                jdbcTemplate.queryForObject(
                    "SELECT password FROM users WHERE username = ?",
                    String::class.java,
                    username
                )
            } catch (e: Exception) {
                throw Exception("Failed to fetch password from the database")
            }

            // Validate username and password (plain text comparison)
            if (storedPassword == null || storedPassword != password) {
                throw Exception("Invalid username or password")
            }

            // Generate JWT token if authentication is successful
            val token = jwtUtil.generateToken(username)

            return AuthResponse(accessToken = token)
        } catch (e: Exception) {
            throw Exception("Failed to login")
        }
    }

    fun register(registerRequest: User): User {
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

            // Insert the new user into the database
            jdbcTemplate.update(
                "INSERT INTO users (id, username, password, created_at) VALUES (?, ?, ?, ?)",
                registerRequest.id ?: UUID.randomUUID().toString(),
                registerRequest.username,
                registerRequest.password,
                registerRequest.createdAt
            )

            return registerRequest
        } catch (e: Exception) {
            println(e.message)
            throw Exception("Failed to register user")
        }
    }

}