package com.quora.auth.controller

import com.quora.auth.model.User
import com.quora.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {
    
    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<User> {
        return ResponseEntity.ok(authService.registerUser(user))
    }
    
    @PostMapping("/login")
    fun login(@RequestBody credentials: LoginRequest): ResponseEntity<TokenResponse> {
        val token = authService.login(credentials.id, credentials.password)
        return ResponseEntity.ok(TokenResponse(token))
    }
    
//    @GetMapping("/me")
//    fun getCurrentUser(@RequestHeader("Authorization") token: String): ResponseEntity<User> {
//        return ResponseEntity.ok(authService.getCurrentUser(token.substring(7)))
//    }
}

data class LoginRequest(
    val id: String,
    val password: String
)

data class TokenResponse(
    val token: String
) 