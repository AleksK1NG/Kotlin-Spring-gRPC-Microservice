package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.interceptors.LogGrpcInterceptor
import com.example.alexbryksin.mappers.BankAccountMapper
import com.example.alexbryksin.service.BankAccountService
import com.example.grpc.bank.service.BankAccount.*
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.util.*


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class BankAccountGrpcService(private val bankAccountService: BankAccountService) :
    BankAccountServiceGrpcKt.BankAccountServiceCoroutineImplBase() {

    override suspend fun createBankAccount(request: CreateBankAccountRequest): CreateBankAccountResponse {
        val bankAccount = BankAccountMapper.bankAccountFromCreateBankAccountGrpcRequest(request)
        val createdBankAccount = bankAccountService.createBankAccount(bankAccount)
            .also { log.info("created bank account: $it") }
        return CreateBankAccountResponse.newBuilder()
            .setBankAccount(BankAccountMapper.bankAccountToProto(createdBankAccount))
            .build()
    }

    override suspend fun getBankAccountById(request: GetBankAccountByIdRequest): GetBankAccountByIdResponse {
        val bankAccount = bankAccountService.getBankAccountById(UUID.fromString(request.id))
        log.info("found bank account: $bankAccount")
        return GetBankAccountByIdResponse.newBuilder().setBankAccount(BankAccountMapper.bankAccountToProto(bankAccount))
            .build()
    }

    override suspend fun depositBalance(request: DepositBalanceRequest): DepositBalanceResponse {
        val bankAccount =
            bankAccountService.depositAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
        return DepositBalanceResponse.newBuilder()
            .setBankAccount(BankAccountMapper.bankAccountToProto(bankAccount))
            .build()
    }

    override suspend fun withdrawBalance(request: WithdrawBalanceRequest): WithdrawBalanceResponse {
        val bankAccount =
            bankAccountService.withdrawAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
        return WithdrawBalanceResponse.newBuilder().setBankAccount(BankAccountMapper.bankAccountToProto(bankAccount))
            .build()
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountGrpcService::class.java)
    }
}