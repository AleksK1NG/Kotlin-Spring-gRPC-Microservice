package com.example.alexbryksin.delivery.http

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.of
import com.example.alexbryksin.domain.toSuccessHttpResponse
import com.example.alexbryksin.dto.CreateBankAccountDto
import com.example.alexbryksin.dto.DepositBalanceDto
import com.example.alexbryksin.dto.SuccessBankAccountResponse
import com.example.alexbryksin.dto.WithdrawBalanceDto
import com.example.alexbryksin.services.BankAccountService
import io.swagger.v3.oas.annotations.Operation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*
import javax.validation.Valid


@RestController
@RequestMapping(path = ["/api/v1/bank"])
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PostMapping
    @Operation(method = "createBankAccount", summary = "create bew bank account", operationId = "createBankAccount")
    suspend fun createBankAccount(@Valid @RequestBody req: CreateBankAccountDto) =
        withTimeout(timeOutMillis) {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankAccountService.createBankAccount(BankAccount.of(req)).toSuccessHttpResponse())
                .also { log.info("created bank account: $it") }
        }

    @PutMapping(path = ["/deposit/{id}"])
    @Operation(method = "depositBalance", summary = "deposit balance", operationId = "depositBalance")
    suspend fun depositBalance(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody depositBalanceDto: DepositBalanceDto
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.depositAmount(id, depositBalanceDto.amount).toSuccessHttpResponse())
            .also { log.info("response: $it") }
    }

    @PutMapping(path = ["/withdraw/{id}"])
    @Operation(method = "withdrawBalance", summary = "withdraw balance", operationId = "withdrawBalance")
    suspend fun withdrawBalance(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody withdrawBalanceDto: WithdrawBalanceDto
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.depositAmount(id, withdrawBalanceDto.amount).toSuccessHttpResponse())
            .also { log.info("response: $it") }
    }

    @GetMapping(path = ["{id}"])
    @Operation(method = "getBankAccountById", summary = "get bank account by id", operationId = "getBankAccountById")
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.getBankAccountById(id).toSuccessHttpResponse())
            .also { log.info("success get bank account: $it") }
    }


    @GetMapping(path = ["all/balance"])
    @Operation(
        method = "findAllAccountsByBalance",
        summary = "find all bank account with given amount range",
        operationId = "findAllAccounts"
    )
    suspend fun findAllAccountsByBalance(
        @RequestParam(name = "min", defaultValue = "0") min: BigDecimal,
        @RequestParam(name = "max", defaultValue = "500000000") max: BigDecimal,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.findByBalanceAmount(min, max, PageRequest.of(page, size)))
            .also { log.info("response: $it") }
    }

    @GetMapping(path = ["all/balance/stream"])
    @Operation(
        method = "getAllByBalanceStream",
        summary = "find all bank account with given amount range returns stream",
        operationId = "getAllByBalanceStream"
    )
    fun getAllByBalanceStream(
        @RequestParam(name = "min", defaultValue = "0") min: BigDecimal,
        @RequestParam(name = "max", defaultValue = "500000000") max: BigDecimal,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ): Flow<SuccessBankAccountResponse> {
        return bankAccountService.findAllByBalanceBetween(min, max, PageRequest.of(page, size))
            .map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }
    }


    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}