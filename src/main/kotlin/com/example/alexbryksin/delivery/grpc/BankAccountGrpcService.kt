package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.of
import com.example.alexbryksin.domain.toProto
import com.example.alexbryksin.interceptors.LogGrpcInterceptor
import com.example.alexbryksin.service.BankAccountService
import com.example.grpc.bank.service.BankAccount.*
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.math.BigDecimal
import java.util.*
import javax.validation.ConstraintViolationException
import javax.validation.Validator


@GrpcService(interceptors = [LogGrpcInterceptor::class])
class BankAccountGrpcService(
    private val bankAccountService: BankAccountService,
    private val tracer: Tracer,
    private val validator: Validator
) :
    BankAccountServiceGrpcKt.BankAccountServiceCoroutineImplBase() {


    override suspend fun createBankAccount(request: CreateBankAccountRequest): CreateBankAccountResponse =
        withContext(tracer.asContextElement()) {
            withTimeout(timeOutMillis) {
                val span = tracer.startScopedSpan("BankAccountGrpcService.createBankAccount")

                try {
                    bankAccountService.createBankAccount(validate(BankAccount.of(request)))
                        .let { CreateBankAccountResponse.newBuilder().setBankAccount(it.toProto()).build() }
                        .also { it ->
                            log.info("created bank account: $it").also { span.tag("account", it.toString()) }
                        }
                } finally {
                    span.end()
                }
            }
        }

    override suspend fun getBankAccountById(request: GetBankAccountByIdRequest): GetBankAccountByIdResponse =
        withContext(tracer.asContextElement()) {
            withTimeout(timeOutMillis) {
                val span = tracer.startScopedSpan("BankAccountGrpcService.getBankAccountById")

                try {
                    bankAccountService.getBankAccountById(UUID.fromString(request.id))
                        .let { GetBankAccountByIdResponse.newBuilder().setBankAccount(it.toProto()).build() }
                        .also { it -> log.info("response: $it").also { span.tag("response", it.toString()) } }
                } finally {
                    span.end()
                }
            }
        }

    override suspend fun depositBalance(request: DepositBalanceRequest): DepositBalanceResponse =
        withContext(tracer.asContextElement()) {
            withTimeout(timeOutMillis) {
                val span = tracer.startScopedSpan("BankAccountGrpcService.depositBalance")

                try {
                    bankAccountService.depositAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
                        .let { DepositBalanceResponse.newBuilder().setBankAccount(it.toProto()).build() }
                        .also { it -> log.info("response: $it").also { span.tag("response", it.toString()) } }
                } finally {
                    span.end()
                }
            }
        }

    override suspend fun withdrawBalance(request: WithdrawBalanceRequest): WithdrawBalanceResponse =
        withContext(tracer.asContextElement()) {
            withTimeout(timeOutMillis) {
                val span = tracer.startScopedSpan("BankAccountGrpcService.withdrawBalance")

                try {
                    bankAccountService.withdrawAmount(UUID.fromString(request.id), BigDecimal.valueOf(request.balance))
                        .let { WithdrawBalanceResponse.newBuilder().setBankAccount(it.toProto()).build() }
                        .also { it -> log.info("response: $it").also { span.tag("response", it.toString()) } }
                } finally {
                    span.end()
                }
            }
        }

    override fun getAllByBalance(request: GetAllByBalanceRequest): Flow<GetAllByBalanceResponse> {
        val span = tracer.startScopedSpan("BankAccountGrpcService.getAllByBalance")

        try {
            return bankAccountService.findAllByBalanceBetween(
                request.min.toBigDecimal(),
                request.max.toBigDecimal(),
                PageRequest.of(request.page, request.size)
            ).map { GetAllByBalanceResponse.newBuilder().setBankAccount(it.toProto()).build() }
        } finally {
            span.end()
        }
    }

    override suspend fun getAllByBalanceWithPagination(request: GetAllByBalanceWithPaginationRequest): GetAllByBalanceWithPaginationResponse =
        withContext(tracer.asContextElement()) {
            withTimeout(timeOutMillis) {
                val span = tracer.startScopedSpan("BankAccountGrpcService.getAllByBalanceWithPagination")

                try {
                    bankAccountService.findByBalanceAmount(
                        request.min.toBigDecimal(),
                        request.max.toBigDecimal(),
                        PageRequest.of(request.page, request.size)
                    )
                        .let { it.toGetAllByBalanceWithPaginationResponse() }
                        .also { log.info("response: $it") }.also { span.tag("response", it.toString()) }
                } finally {
                    span.end()
                }
            }

        }


    private fun <T> validate(data: T): T {
        val errors = validator.validate(data)
        if (errors.isNotEmpty()) throw ConstraintViolationException(errors).also { log.error("validation error: ${it.localizedMessage}") }
        return data
    }


    companion object {
        private val log = LoggerFactory.getLogger(BankAccountGrpcService::class.java)
        private const val timeOutMillis = 5000L
    }
}

fun PageImpl<BankAccount>.toGetAllByBalanceWithPaginationResponse(): GetAllByBalanceWithPaginationResponse {
    return GetAllByBalanceWithPaginationResponse
        .newBuilder()
        .setIsFirst(this.isFirst)
        .setIsLast(this.isLast)
        .setTotalElements(this.totalElements.toInt())
        .setTotalPages(this.totalPages)
        .setPage(this.pageable.pageNumber)
        .setSize(this.pageable.pageSize)
        .addAllBankAccount(this.content.map { it.toProto() })
        .build()
}