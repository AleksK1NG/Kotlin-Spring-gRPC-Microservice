package com.example.alexbryksin.dto

import com.example.alexbryksin.domain.Currency
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class SuccessBankAccountResponse(
    val id: UUID?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val address: String?,
    val phone: String?,
    val currency: Currency?,
    val balance: BigDecimal?,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
)
