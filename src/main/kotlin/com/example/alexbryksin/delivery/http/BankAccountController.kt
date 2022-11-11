package com.example.alexbryksin.delivery.http

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.of
import com.example.alexbryksin.domain.toSuccessHttpResponse
import com.example.alexbryksin.dto.*
import com.example.alexbryksin.services.BankAccountService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.util.*
import javax.validation.Valid


@Tag(name = "BankAccount", description = "Bank Account REST Controller")
@RestController
@RequestMapping(path = ["/api/v1/bank"])
class BankAccountController(private val bankAccountService: BankAccountService) {

    @PostMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "createBankAccount",
        summary = "Create bew bank account",
        operationId = "createBankAccount",
        description = "Create new bank for account for user"
    )
    suspend fun createBankAccount(@Valid @RequestBody req: CreateBankAccountDto) =
        withTimeout(timeOutMillis) {
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bankAccountService.createBankAccount(BankAccount.of(req)).toSuccessHttpResponse())
                .also { log.info("created bank account: $it") }
        }

    @PutMapping(path = ["/deposit/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "depositBalance",
        summary = "Deposit balance",
        operationId = "depositBalance",
        description = "Deposit given amount to the bank account balance"
    )
    suspend fun depositBalance(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody depositBalanceDto: DepositBalanceDto
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.depositAmount(id, depositBalanceDto.amount).toSuccessHttpResponse())
            .also { log.info("response: $it") }
    }

    @PutMapping(path = ["/withdraw/{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "withdrawBalance",
        summary = "Withdraw balance",
        operationId = "withdrawBalance",
        description = "Withdraw given amount from the bank account balance"
    )
    suspend fun withdrawBalance(
        @PathVariable("id") id: UUID,
        @Valid @RequestBody withdrawBalanceDto: WithdrawBalanceDto
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.depositAmount(id, withdrawBalanceDto.amount).toSuccessHttpResponse())
            .also { log.info("response: $it") }
    }

    @GetMapping(path = ["{id}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "getBankAccountById",
        summary = "Get bank account by id",
        operationId = "getBankAccountById",
        description = "Get user bank account by given id"
    )
    suspend fun getBankAccountById(@PathVariable(required = true) id: UUID) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.getBankAccountById(id).toSuccessHttpResponse())
            .also { log.info("success get bank account: $it") }
    }


    @GetMapping(path = ["all/balance"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        method = "findAllAccountsByBalance",
        summary = "Find all bank account with given amount range",
        operationId = "findAllAccounts",
        description = "Find all bank accounts for the given balance range with pagination"
    )
    suspend fun findAllAccountsByBalance(
        @RequestParam(name = "min", defaultValue = "0") min: BigDecimal,
        @RequestParam(name = "max", defaultValue = "500000000") max: BigDecimal,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ) = withTimeout(timeOutMillis) {
        ResponseEntity.ok(bankAccountService.findByBalanceAmount(FindByBalanceRequestDto(min, max, PageRequest.of(page, size))))
            .also { log.info("response: $it") }
    }

    @GetMapping(path = ["all/balance/stream"])
    @Operation(
        method = "getAllByBalanceStream",
        summary = "Find all bank account with given amount range returns stream",
        operationId = "getAllByBalanceStream",
        description = "Find all bank accounts for the given balance range"
    )
    fun getAllByBalanceStream(
        @RequestParam(name = "min", defaultValue = "0") min: BigDecimal,
        @RequestParam(name = "max", defaultValue = "500000000") max: BigDecimal,
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int,
    ): Flow<SuccessBankAccountResponse> {
        return bankAccountService.findAllByBalanceBetween(FindByBalanceRequestDto(min, max, PageRequest.of(page, size)))
            .map { it -> it.toSuccessHttpResponse().also { log.info("response: $it") } }
    }


    companion object {
        private val log = LoggerFactory.getLogger(BankAccountController::class.java)
        private const val timeOutMillis = 5000L
    }
}