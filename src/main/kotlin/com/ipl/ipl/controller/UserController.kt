package com.ipl.ipl.controller

import com.ipl.ipl.Service.UserService
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.RefreshTokenRequest
import com.ipl.ipl.model.RegisterRequest
import com.ipl.ipl.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["http://localhost:3001"])
@RequestMapping("/api/auth")
class UserController (
    private val userService: UserService
) {
    @PostMapping("/login")
    fun login(@RequestBody loginRequest: User): ResponseEntity<AuthResponse> {
        // Implement login logic here
        return ResponseEntity.ok(userService.login(loginRequest.username, loginRequest.password))
    }

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<User> {
        // Implement registration logic here
        return ResponseEntity.ok(userService.register(registerRequest))
    }

    @PostMapping("/get/user")
    fun getUserDetails(@RequestHeader("Authorization") authorization: String): ResponseEntity<User> {
        // Implement logic to get user details
        return ResponseEntity.ok(userService.getUserDetails(authorization))
    }

    @PostMapping("/refresh-token")
    fun refreshToken(@RequestBody refreshTokenRequest: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        // Implement logic to refresh the access token
        return ResponseEntity.ok(userService.refreshToken(refreshTokenRequest.refreshToken))
    }
}