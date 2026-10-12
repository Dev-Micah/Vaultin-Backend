package com.micah.vaultin.model.entities

import com.micah.vaultin.model.AccountType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "accounts")
class Account(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    @Column(name= "account_type", nullable = false, updatable = false)
    val accountType: AccountType,

    @Column(nullable = false, updatable = false)
    val currency: String = "KES",

    @Column(name = "cached_balance", nullable = false,precision = 19, scale = 4)
    var cachedBalance: BigDecimal= BigDecimal.ZERO,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

    @Version
    @Column(nullable = false)
    var version: Long = 0

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()
}