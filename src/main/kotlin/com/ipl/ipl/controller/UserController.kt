package com.ipl.ipl.controller

import com.ipl.ipl.Service.UserService
import com.ipl.ipl.model.AuthResponse
import com.ipl.ipl.model.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin
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
    fun register(@RequestBody registerRequest: User): ResponseEntity<User> {
        // Implement registration logic here
        return ResponseEntity.ok(userService.register(registerRequest))
    }
}