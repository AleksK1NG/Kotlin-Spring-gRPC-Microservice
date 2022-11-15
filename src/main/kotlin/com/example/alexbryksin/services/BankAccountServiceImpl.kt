package com.example.alexbryksin.services

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.dto.FindByBalanceRequestDto
import com.example.alexbryksin.exceptions.BankAccountNotFoundException
import com.example.alexbryksin.repositories.BankRepository
import com.example.alexbryksin.utils.runWithTracing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*
import javax.validation.Valid

@Service
class BankAccountServiceImpl(
    private val bankRepository: BankRepository,
    private val tracer: Tracer
) : BankAccountService {

    @Transactional
    override suspend fun createBankAccount(@Valid bankAccount: BankAccount): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.startScopedSpan(CREATE_BANK_ACCOUNT)

            runWithTracing(span) {
                bankRepository.save(bankAccount).also { span.tag("saved account", it.toString()) }
            }
        }

    @Transactional(readOnly = true)
    override suspend fun getBankAccountById(id: UUID): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.startScopedSpan(GET_BANK_ACCOUNT_BY_ID)

            runWithTracing(span) {
                bankRepository.findById(id).also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())
            }
        }

    @Transactional
    override suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.startScopedSpan(DEPOSIT_AMOUNT)

            runWithTracing(span) {
                bankRepository.findById(id)
                    ?.let { bankRepository.save(it.depositAmount(amount)) }
                    .also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())
            }
        }

    @Transactional
    override suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.startScopedSpan(WITHDRAW_AMOUNT)

            runWithTracing(span) {
                bankRepository.findById(id)
                    ?.let { bankRepository.save(it.withdrawAmount(amount)) }
                    .also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())
            }
        }

    @Transactional(readOnly = true)
    override fun findAllByBalanceBetween(requestDto: FindByBalanceRequestDto): Flow<BankAccount> {
        val span = tracer.startScopedSpan(GET_ALL_BY_BALANCE)

        runWithTracing(span) {
            return bankRepository.findAllByBalanceBetween(
                requestDto.minBalance,
                requestDto.maxBalance,
                requestDto.pageable
            )
        }
    }

    @Transactional(readOnly = true)
    override suspend fun findByBalanceAmount(requestDto: FindByBalanceRequestDto): Page<BankAccount> =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.startScopedSpan(GET_ALL_BY_BALANCE_WITH_PAGINATION)

            runWithTracing(span) {
                bankRepository.findByBalanceAmount(requestDto.minBalance, requestDto.maxBalance, requestDto.pageable)
                    .also { span.tag("pagination", it.toString()) }
            }
        }


    companion object {
        private const val CREATE_BANK_ACCOUNT = "BankAccountService.createBankAccount"
        private const val GET_BANK_ACCOUNT_BY_ID = "BankAccountService.getBankAccountById"
        private const val DEPOSIT_AMOUNT = "BankAccountService.depositAmount"
        private const val WITHDRAW_AMOUNT = "BankAccountService.withdrawAmount"
        private const val GET_ALL_BY_BALANCE = "BankAccountService.findAllByBalanceBetween"
        private const val GET_ALL_BY_BALANCE_WITH_PAGINATION = "BankAccountService.findByBalanceAmount"
    }
}