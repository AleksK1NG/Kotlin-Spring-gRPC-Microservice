package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*


@Repository
interface BankRepository : CoroutineSortingRepository<BankAccount, UUID>, BankPostgresRepository {

    suspend fun findByEmail(email: String): BankAccount?

    fun findAllByBalanceBetween(min: BigDecimal, max: BigDecimal, pageable: Pageable): Flow<BankAccount>
}