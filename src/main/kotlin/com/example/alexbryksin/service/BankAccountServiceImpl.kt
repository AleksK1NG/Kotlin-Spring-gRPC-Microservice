package com.example.alexbryksin.service

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.exceptions.BankAccountNotFoundException
import com.example.alexbryksin.repositories.BankRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
class BankAccountServiceImpl(val bankRepository: BankRepository) : BankAccountService {


    @Transactional
    override suspend fun createBankAccount(bankAccount: BankAccount): BankAccount = withContext(Dispatchers.IO) {
        try {
            bankRepository.save(bankAccount).also { log.info("saved bank account: $it") }
        } catch (ex: Exception) {
            log.error("error", ex)
            throw ex
        }
    }

    @Transactional(readOnly = true)
    override suspend fun getBankAccountById(id: UUID): BankAccount = withContext(Dispatchers.IO) {
        bankRepository.findById(id)?: throw BankAccountNotFoundException(id.toString())
    }

    @Transactional
    override suspend fun depositAmount(id: UUID, amount: BigDecimal): BankAccount = withContext(Dispatchers.IO) {
        val bankAccount = bankRepository.findById(id) ?: throw BankAccountNotFoundException(id.toString())
        bankAccount.depositAmount(amount)
        bankRepository.save(bankAccount).also { log.info("depositAmount bank account: $it") }
    }

    @Transactional
    override suspend fun withdrawAmount(id: UUID, amount: BigDecimal): BankAccount = withContext(Dispatchers.IO) {
        val bankAccount = bankRepository.findById(id) ?: throw BankAccountNotFoundException(id.toString())
        bankAccount.withdrawAmount(amount)
        bankRepository.save(bankAccount).also { log.info("withdrawAmount bank account: $it") }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankAccountServiceImpl::class.java)
    }
}