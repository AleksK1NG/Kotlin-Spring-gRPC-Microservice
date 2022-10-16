package com.example.alexbryksin.domain

import com.example.grpc.bank.service.BankAccount.BankAccountData
import java.math.BigDecimal

data class BankAccount(
    var id: String = "",
    var email: String = "",
    var name: String = "",
    var currency: String = "",
    var balance: BigDecimal = BigDecimal.ZERO
) {
    companion object

    fun depositAmount(amount: BigDecimal) {
        balance = balance.plus(amount)
    }

    fun toProto(): BankAccountData {
        return BankAccountData.newBuilder()
            .setId(this.id)
            .setEmail(this.email)
            .setName(this.name)
            .setCurrency(this.currency)
            .setBalance(this.balance.toDouble())
            .build()
    }
}

fun BankAccount.Companion.of(bankAccountData: BankAccountData): BankAccount {
    return BankAccount(
        id = "",
        email = bankAccountData.email,
        name = bankAccountData.name,
        currency = bankAccountData.currency,
        balance = BigDecimal.valueOf(bankAccountData.balance)
    )
}

fun BankAccount.Companion.of(id: String, bankAccountData: BankAccountData): BankAccount {
    return BankAccount(
        id = id,
        email = bankAccountData.email,
        name = bankAccountData.name,
        currency = bankAccountData.currency,
        balance = BigDecimal.valueOf(bankAccountData.balance)
    )
}

