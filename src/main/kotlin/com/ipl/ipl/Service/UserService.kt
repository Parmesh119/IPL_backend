package com.ipl.ipl.Service

import com.ipl.ipl.Repository.UserRepository
import com.ipl.ipl.config.JwtUtil
import com.ipl.ipl.controller.UsernameAlreadyExistsException
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.RegisterRequest
import com.ipl.ipl.model.User
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil
) {
    fun login(username: String, password: String): AuthResponse {
        // Implement login logic here
        return userRepository.login(username, password)
    }

    fun register(registerRequest: RegisterRequest): Any {
        try {
            return userRepository.register(registerRequest)
        } catch (e: UsernameAlreadyExistsException) {
            throw UsernameAlreadyExistsException("Username already exists")
        } catch (e: Exception) {
            throw Exception("Failed to register user")
        }
        // Implement registration logic here

    }

    fun getUserDetails(authorization: String): User {

        val token = authorization.substring(7)
        val username = jwtUtil.getSubjectFromToken(token)

        // Implement logic to get user details
        return userRepository.getUserDetails(username)
    }

    fun refreshToken(refreshToken: String): AuthResponse {
        // Implement logic to refresh the access token
        return userRepository.refreshToken(refreshToken)
    }
}