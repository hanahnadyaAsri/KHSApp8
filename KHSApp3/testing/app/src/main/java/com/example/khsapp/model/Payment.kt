package com.example.khsapp.model

enum class PaymentStatus {
    PENDING, COMPLETED, FAILED, REFUNDED
}

data class Payment(
    val id: String = "",
    val bookingId: String = "",
    val amount: Double = 0.0,
    val method: PaymentMethod = PaymentMethod.CREDIT_CARD,
    val status: PaymentStatus = PaymentStatus.PENDING,
    val transactionId: String = ""
)
