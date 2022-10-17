package com.example.alexbryksin.mappers

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.Currency
import com.example.grpc.bank.service.BankAccount.BankAccountData
import com.example.grpc.bank.service.BankAccount.CreateBankAccountRequest
import java.math.BigDecimal
import java.time.LocalDateTime

object BankAccountMapper {

    fun bankAccountFromCreateBankAccountGrpcRequest(request: CreateBankAccountRequest): BankAccount {
        return BankAccount(
            id = null,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            address = request.address,
            phone = request.phone,
            currency = Currency.valueOf(request.currency),
            balance = BigDecimal.valueOf(request.balance),
            updatedAt = LocalDateTime.now(),
            createdAt = LocalDateTime.now(),
        )
    }

    fun bankAccountToProto(bankAccount: BankAccount): BankAccountData {
        return BankAccountData.newBuilder()
            .setId(bankAccount.id.toString())
            .setEmail(bankAccount.email)
            .setFirstName(bankAccount.firstName)
            .setLastName(bankAccount.lastName)
            .setAddress(bankAccount.address)
            .setPhone(bankAccount.phone)
            .setBalance(bankAccount.balance.toDouble())
            .setCurrency(bankAccount.currency.name)
            .setUpdatedAt(bankAccount.updatedAt.toString())
            .setCreatedAt(bankAccount.createdAt.toString())
            .build()
    }
}