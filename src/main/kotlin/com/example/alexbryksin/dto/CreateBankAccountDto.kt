package com.example.alexbryksin.dto

import com.example.alexbryksin.domain.Currency
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Size

data class CreateBankAccountDto(
    @get:Email @get:Size(min = 6, max = 60) var email: String,
    @get:Size(min = 3, max = 60) val firstName: String,
    @get:Size(min = 3, max = 60) val lastName: String,
    @get:Size(min = 3, max = 500) val address: String,
    @get:Size(min = 6, max = 20) val phone: String,
    var currency: Currency,
    @get:DecimalMin(value = "0.0") val balance: BigDecimal,
)
