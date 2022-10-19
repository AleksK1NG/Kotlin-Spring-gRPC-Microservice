package com.example.alexbryksin.services

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.exceptions.BankAccountNotFoundException
import com.example.alexbryksin.repositories.BankRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.createBankAccount")

            try {
                bankRepository.save(bankAccount).also { span.tag("saved bank account", it.toString()) }
            } finally {
                span.end()
            }
        }

    @Transactional(readOnly = true)
    override suspend fun getBankAccountById(id: UUID): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.getBankAccountById")

            try {
                bankRepository.findById(id).also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())
            } finally {
                span.end()
            }
        }

    @Transactional
    override suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.depositAmount")

            try {
                bankRepository.findById(id)
                    ?.let { bankRepository.save(it.depositAmount(amount)) }
                    .also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())
            } finally {
                span.end()
            }
        }

    @Transactional
    override suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount =
        withContext(Dispatchers.IO + tracer.asContextElement()) {
            val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.withdrawAmount")

            try {
                bankRepository.findById(id)
                    ?.let { bankRepository.save(it.withdrawAmount(amount)) }
                    .also { span.tag("bank account", it.toString()) }
                    ?: throw BankAccountNotFoundException(id.toString())

            } finally {
                span.end()
            }
        }

    override fun findAllByBalanceBetween(min: BigDecimal, max: BigDecimal, pageable: Pageable): Flow<BankAccount> {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.findAllByBalanceBetween")

        try {
            return bankRepository.findAllByBalanceBetween(min, max, pageable)
        } finally {
            span.end()
        }
    }

    override suspend fun findByBalanceAmount(
        min: BigDecimal,
        max: BigDecimal,
        pageable: Pageable
    ): PageImpl<BankAccount> = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankAccountService.findByBalanceAmount")

        try {
            bankRepository.findByBalanceAmount(min, max, pageable)
                .also { span.tag("pagination", it.toString()) }
        } finally {
            span.end()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountServiceImpl::class.java)
    }
}