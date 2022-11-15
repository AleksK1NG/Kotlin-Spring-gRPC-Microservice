package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
interface BankPostgresRepository {

    suspend fun findByBalanceAmount(min: BigDecimal, max: BigDecimal, pageable: Pageable): Page<BankAccount>

}