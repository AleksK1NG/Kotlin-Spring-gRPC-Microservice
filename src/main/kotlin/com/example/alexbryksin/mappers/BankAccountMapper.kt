package com.example.alexbryksin.mappers

import com.example.grpc.bank.service.BankAccount.BankAccountData
import com.example.grpc.bank.service.BankAccount.CreateBankAccountRequest

object BankAccountMapper {

    fun bankAccountDataFromCreateRequest(createBankAccountRequest: CreateBankAccountRequest): BankAccountData {
        return BankAccountData.newBuilder()
            .setEmail(createBankAccountRequest.email)
            .setName(createBankAccountRequest.name)
            .setCurrency(createBankAccountRequest.currency)
            .setBalance(createBankAccountRequest.balance)
            .build()
    }
}