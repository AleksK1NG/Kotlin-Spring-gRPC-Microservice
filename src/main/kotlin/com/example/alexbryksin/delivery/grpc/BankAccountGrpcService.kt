package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.exceptions.BankAccountNotFoundException
import com.example.alexbryksin.interceptors.LogGrpcInterceptor
import com.example.grpc.bank.service.BankAccountOuterClass.*
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class BankAccountGrpcService : BankAccountServiceGrpcKt.BankAccountServiceCoroutineImplBase() {

    private val repository = ConcurrentHashMap<String, BankAccount>()

    override suspend fun createBankAccount(request: CreateBankAccountRequest): CreateBankAccountResponse {
        val bankAccount = BankAccount.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setEmail(request.email)
            .setBalance(request.balance)
            .setName(request.name)
            .setCurrency(request.currency)
            .build()

        repository[bankAccount.id] = bankAccount
        log.info("created bank account: $bankAccount")
        log.info("repository: $repository")
        return CreateBankAccountResponse.newBuilder().setBankAccount(bankAccount).build()
    }

    override suspend fun getBankAccountById(request: GetBankAccountByIdRequest): GetBankAccountByIdResponse {
        val bankAccount = repository[request.id] ?: throw BankAccountNotFoundException(request.id)
        return GetBankAccountByIdResponse.newBuilder().setBankAccount(bankAccount).build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountGrpcService::class.java)
    }
}