package com.micah.vaultin.model.entities

import com.micah.vaultin.model.PostingDirection
import com.micah.vaultin.model.TransactionStatus
import com.micah.vaultin.model.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import kotlin.collections.fold

@Entity
@Table(name = "transactions")
class Transaction(
    @Column(name = "idempotency_key", nullable = false, unique = true, updatable = false)
    val idempotencyKey: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, updatable = false)
    val transactionType: TransactionType,

    @Column(updatable = false)
    val description: String?,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TransactionStatus = TransactionStatus.PENDING,

    @OneToMany(mappedBy = "transaction", cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    val postings: MutableList<Posting> = mutableListOf()
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()

    /**
     * Helper method to safely anchor child postings to this transaction parent.
     */
    fun addPosting(posting: Posting) {
        postings.add(posting)
        posting.transaction = this
    }

    /**
     * Rigid Accounting Guardrail: Prevents saving unbalanced data.
     * Assuming Credits are positive numbers and Debits are negative numbers,
     * the total sum of all postings MUST equal exactly ZERO.
     */
    @PrePersist
    @PreUpdate
    private fun validateDoubleEntry() {
        if (postings.isEmpty()) {
            throw IllegalStateException("A ledger transaction must contain at least one posting entry.")
        }

        val netSum = postings.fold(BigDecimal.ZERO) { acc, posting ->
            acc.add(posting.amount)
        }

        if (netSum.compareTo(BigDecimal.ZERO) != 0) {
            throw IllegalStateException("Ledger mismatch! Transaction is unbalanced. Net sum is $netSum instead of 0.0000")
        }
    }
}


@Entity
@Table(name = "postings")
class Posting(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    val account: Account,

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    val amount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    val direction: PostingDirection
) {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = false)
    lateinit var transaction: Transaction // Securely initialized via Transaction.addPosting()

    @Column(name = "created_at", updatable = false)
    val createdAt: Instant = Instant.now()

    /**
     * Converts the absolute amount into a signed value for double-entry validation.
     * Standard ledger convention:
     * Credits increase a liability/equity/revenue balance (Positive value).
     * Debits decrease or offset it (Negative value).
     */
    val signedAmount: BigDecimal
        get() = if (direction == PostingDirection.CREDIT) amount else amount.negate()
}
