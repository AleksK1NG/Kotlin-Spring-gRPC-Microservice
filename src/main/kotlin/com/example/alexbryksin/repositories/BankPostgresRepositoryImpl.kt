package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class BankPostgresRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val tracer: Tracer,
) : BankPostgresRepository {

    override suspend fun findByBalanceAmount(
        min: BigDecimal,
        max: BigDecimal,
        pageable: Pageable
    ): PageImpl<BankAccount> = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankPostgresRepository.findByBalanceAmount")
        val query = Query.query(Criteria.where("balance").between(min, max))

        try {
            val accountsList = async {
                template.select(query.with(pageable), BankAccount::class.java)
                    .asFlow()
                    .buffer(accountsListBufferSize)
                    .toList()
            }
            val totalCount = async { template.select(query, BankAccount::class.java).count().awaitFirst() }

            PageImpl(accountsList.await(), pageable, totalCount.await())
                .also { span.tag("PageImpl", it.toString()) }
        } finally {
            span.end()
        }
    }

    companion object {
        const val accountsListBufferSize = 100
    }
}