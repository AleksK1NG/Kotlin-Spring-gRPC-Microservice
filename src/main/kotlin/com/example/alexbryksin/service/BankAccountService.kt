package com.example.alexbryksin.service

import com.example.alexbryksin.domain.BankAccount
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
interface BankAccountService {

    suspend fun createBankAccount(bankAccount: BankAccount): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount

    suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount

    suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount

    fun findAllByBalanceBetween(min: BigDecimal, max: BigDecimal, pageable: Pageable): Flow<BankAccount>

    suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): PageImpl<BankAccount>
}