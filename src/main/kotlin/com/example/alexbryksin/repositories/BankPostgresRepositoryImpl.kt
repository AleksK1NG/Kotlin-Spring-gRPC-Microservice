package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class BankPostgresRepositoryImpl(private val template: R2dbcEntityTemplate) : BankPostgresRepository {
    override suspend fun findByBalanceAmount(
        min: BigDecimal,
        max: BigDecimal,
        pageable: Pageable
    ): PageImpl<BankAccount> = withContext(Dispatchers.IO) {
        val query = Query.query(Criteria.where("balance").between(min, max))

        val accountsList = async {
            template.select(query.with(pageable), BankAccount::class.java)
                .asFlow()
                .buffer(accountsListBufferSize)
                .toList()
        }

        val totalCount = async { template.select(query, BankAccount::class.java).count().awaitFirst() }

        PageImpl(accountsList.await(), pageable, totalCount.await())
    }

    companion object {
        const val accountsListBufferSize = 100
    }
}