package com.example.alexbryksin.services

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.dto.FindByBalanceRequestDto
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageImpl
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
interface BankAccountService {

    suspend fun createBankAccount(bankAccount: BankAccount): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount

    suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount

    suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount

    fun findAllByBalanceBetween(requestDto: FindByBalanceRequestDto): Flow<BankAccount>

    suspend fun findByBalanceAmount(requestDto: FindByBalanceRequestDto): PageImpl<BankAccount>
}