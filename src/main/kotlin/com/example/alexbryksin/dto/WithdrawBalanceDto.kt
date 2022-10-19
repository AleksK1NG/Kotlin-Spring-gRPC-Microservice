package com.example.alexbryksin.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

data class WithdrawBalanceDto(@get:DecimalMin(value = "0.0") val amount: BigDecimal)
