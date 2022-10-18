package com.example.alexbryksin.service

import com.example.alexbryksin.domain.BankAccount
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*


@Service
interface BankAccountService {

    suspend fun createBankAccount(bankAccount: BankAccount): BankAccount

    suspend fun getBankAccountById(id: UUID): BankAccount

    suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount

    suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount
}