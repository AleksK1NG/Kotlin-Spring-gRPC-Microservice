package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.domain.toProto
import com.example.alexbryksin.interceptors.LogGrpcInterceptor
import com.example.alexbryksin.mappers.BankAccountMapper
import com.example.alexbryksin.service.BankAccountService
import com.example.grpc.bank.service.BankAccount.*
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.util.*


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class BankAccountGrpcService(private val bankAccountService: BankAccountService) :
    BankAccountServiceGrpcKt.BankAccountServiceCoroutineImplBase() {

    override suspend fun createBankAccount(request: CreateBankAccountRequest): CreateBankAccountResponse =
        withTimeout(timeOutMillis) {
            bankAccountService.createBankAccount(BankAccountMapper.bankAccountFromCreateBankAccountGrpcRequest(request))
                .let { CreateBankAccountResponse.newBuilder().setBankAccount(it.toProto()).build() }
                .also { log.info("created bank account: $it") }
        }

    override suspend fun getBankAccountById(request: GetBankAccountByIdRequest): GetBankAccountByIdResponse =
        withTimeout(timeOutMillis) {
            bankAccountService.getBankAccountById(UUID.fromString(request.id))
                .let { GetBankAccountByIdResponse.newBuilder().setBankAccount(it.toProto()).build() }
                .also { log.info("found bank account: $it") }
        }

    override suspend fun depositBalance(request: DepositBalanceRequest): DepositBalanceResponse =
        withTimeout(timeOutMillis) {
            bankAccountService.depositAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
                .let { DepositBalanceResponse.newBuilder().setBankAccount(it.toProto()).build() }
                .also { log.info("response: $it") }
        }

    override suspend fun withdrawBalance(request: WithdrawBalanceRequest): WithdrawBalanceResponse =
        withTimeout(timeOutMillis) {
            bankAccountService.withdrawAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
                .let { WithdrawBalanceResponse.newBuilder().setBankAccount(it.toProto()).build() }
                .also { log.info("response: $it") }
        }

    override fun getAllByBalance(request: GetAllByBalanceRequest): Flow<GetAllByBalanceResponse> {
        return bankAccountService.findAllByBalanceBetween(
            request.min.toBigDecimal(),
            request.max.toBigDecimal(),
            PageRequest.of(request.page, request.size)
        ).map {
            GetAllByBalanceResponse
                .newBuilder()
                .setBankAccount(it.toProto())
                .build()
        }
    }

    override suspend fun getAllByBalanceWithPagination(request: GetAllByBalanceWithPaginationRequest): GetAllByBalanceWithPaginationResponse =
        withTimeout(timeOutMillis) {
            bankAccountService.findByBalanceAmount(
                request.min.toBigDecimal(),
                request.max.toBigDecimal(),
                PageRequest.of(request.page, request.size)
            ).let { it ->
                GetAllByBalanceWithPaginationResponse
                    .newBuilder()
                    .setIsFirst(it.isFirst)
                    .setIsLast(it.isLast)
                    .setTotalElements(it.totalElements.toInt())
                    .setTotalPages(it.totalPages)
                    .setPage(it.pageable.pageNumber)
                    .setSize(it.pageable.pageSize)
                    .addAllBankAccount(it.content.map { it.toProto() })
                    .build()
            }
        }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountGrpcService::class.java)
        private const val timeOutMillis = 5000L
    }
}