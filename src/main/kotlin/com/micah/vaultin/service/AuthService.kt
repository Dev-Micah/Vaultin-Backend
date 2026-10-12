package com.micah.vaultin.service

import com.micah.vaultin.dto.AuthResponse
import com.micah.vaultin.dto.LoginRequest
import com.micah.vaultin.dto.RegisterRequest
import com.micah.vaultin.model.UserRole
import com.micah.vaultin.model.entities.User
import com.micah.vaultin.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService
) {

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {

        if (userRepository.existsByEmail(request.email)){
            throw IllegalArgumentException("Email already exists")
        }

        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            role = UserRole.MEMBER
        )

        val savedUser = userRepository.save(user)
        val token = tokenService.generateToken(savedUser)

        return AuthResponse(accessToken = token)
    }

    @org.springframework.transaction.annotation.Transactional(readOnly=true)
    fun login(request: LoginRequest): AuthResponse{

        val user = userRepository.findByEmail(request.email)

        val passwordMatches = if (user != null){
            passwordEncoder.matches(request.password, user.passwordHash)
        } else{
            passwordEncoder.matches(request.password, "dummy_hash_to_prevent_timing_attack")
            false
        }

        if(!passwordMatches){
            throw BadCredentialsException("Invalid email or password")
        }

        val token = tokenService.generateToken(user!!)
        return AuthResponse(accessToken = token)
    }
}