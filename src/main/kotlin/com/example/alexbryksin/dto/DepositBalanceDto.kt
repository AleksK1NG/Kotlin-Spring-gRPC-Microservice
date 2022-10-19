package com.example.alexbryksin.dto

import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

class DepositBalanceDto(@get:DecimalMin(value = "0.0") val amount: BigDecimal)