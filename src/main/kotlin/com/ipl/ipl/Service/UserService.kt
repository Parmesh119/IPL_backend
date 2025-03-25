package com.ipl.ipl.Service

import com.ipl.ipl.Repository.UserRepository
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.User
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository
) {
    fun login(username: String, password: String): AuthResponse {
        // Implement login logic here
        return userRepository.login(username, password)
    }

    fun register(registerRequest: User): User {
        // Implement registration logic here
        return userRepository.register(registerRequest)
    }
}