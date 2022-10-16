package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.interceptors.LogGrpcInterceptor
import com.example.grpc.bank.service.BankAccount
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import java.util.*


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class BankAccountGrpcService : BankAccountServiceGrpcKt.BankAccountServiceCoroutineImplBase() {

    override suspend fun createBankAccount(request: BankAccount.CreateBankAccountRequest): BankAccount.CreateBankAccountResponse {
        if (request.balance < 0) throw RuntimeException("invalid amount: ${request.balance}")

        val bankAccountResponse = BankAccount.CreateBankAccountResponse.newBuilder()
            .setName(request.name)
            .setEmail(request.email)
            .setId(UUID.randomUUID().toString())
            .setCurrency(request.currency)
            .setBalance(request.balance)
            .build()

        log.info("created bank account: $bankAccountResponse")
        return bankAccountResponse
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountGrpcService::class.java)
    }
}