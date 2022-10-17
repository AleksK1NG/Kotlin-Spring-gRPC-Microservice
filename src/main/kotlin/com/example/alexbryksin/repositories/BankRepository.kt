package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import org.springframework.data.repository.kotlin.CoroutineSortingRepository
import java.util.*

interface BankRepository : CoroutineSortingRepository<BankAccount, UUID> {

    suspend fun findByEmail(email: String): BankAccount?
}