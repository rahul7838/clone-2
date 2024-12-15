package com.quora.auth.service

import com.quora.auth.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import com.quora.auth.security.UserPrincipal
import com.quora.auth.repository.UserRepository
import com.quora.auth.security.JwtUtil
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.findByIdOrNull

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder,
    @Lazy private val authenticationManager: AuthenticationManager
) : UserDetailsService {
    
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findById(username)
            .orElseThrow { UsernameNotFoundException("User not found") }
        return UserPrincipal(user)
    }
    
    fun registerUser(user: User): User {
        if (userRepository.findByEmail(user.email) != null) {
            throw RuntimeException("User already exists")
        }
        
        val hashedPassword = passwordEncoder.encode(user.password)
        return userRepository.save(user.copy(password = hashedPassword))
    }

    fun login(id: String, password: String): String {
        // Authenticate user
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(id, password)
        )
        
        // If authentication successful, generate token
        val user = userRepository.findByIdOrNull(id)
            ?: throw RuntimeException("User not found")
            
        return jwtUtil.generateToken(user)
    }
} 