package com.example.alexbryksin.dto

import com.example.grpc.bank.service.BankAccount
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.math.BigDecimal
import javax.validation.constraints.DecimalMin

data class FindByBalanceRequestDto(
    @get:DecimalMin(value = "0.0") val minBalance: BigDecimal,
    @get:DecimalMin(value = "1.0") val maxBalance: BigDecimal,
    val pageable: Pageable
) {
    companion object
}


fun FindByBalanceRequestDto.Companion.of(request: BankAccount.GetAllByBalanceWithPaginationRequest): FindByBalanceRequestDto =
    FindByBalanceRequestDto(request.min.toBigDecimal(), request.max.toBigDecimal(), PageRequest.of(request.page, request.size))

fun FindByBalanceRequestDto.Companion.of(request: BankAccount.GetAllByBalanceRequest): FindByBalanceRequestDto =
    FindByBalanceRequestDto(request.min.toBigDecimal(), request.max.toBigDecimal(), PageRequest.of(request.page, request.size))