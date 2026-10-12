package com.micah.vaultin.model

enum class UserRole{
    MEMBER, ADMIN
}

enum class UserStatus{
    ACTIVE, SUSPENDED, PENDING_KYC
}

enum class AccountType{
    SAVINGS, SHARE_CAPITAL, LOAN
}

enum class TransactionType{
    DEPOSIT, WITHDRAWAL, TRANSFER
}

enum class TransactionStatus{
    PENDING, SUCCESSFUL, FAILED
}

enum class PostingDirection{
    CREDIT, DEBIT
}