package com.micah.vaultin.repository

import com.micah.vaultin.model.entities.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID>{
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}