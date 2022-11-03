package com.example.alexbryksin.repositories

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.BankAccount.Companion.BALANCE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.cloud.sleuth.Tracer
import org.springframework.cloud.sleuth.instrument.kotlin.asContextElement
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.math.BigDecimal


@Repository
class BankPostgresRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient,
    private val tracer: Tracer,
) : BankPostgresRepository {

    override suspend fun findByBalanceAmount(
        min: BigDecimal,
        max: BigDecimal,
        pageable: Pageable
    ): PageImpl<BankAccount> = withContext(Dispatchers.IO + tracer.asContextElement()) {
        val span = tracer.nextSpan(tracer.currentSpan()).start().name("BankPostgresRepository.findByBalanceAmount")
        val query = Query.query(Criteria.where(BALANCE).between(min, max))

        try {
            val accountsList = async {
                template.select(query.with(pageable), BankAccount::class.java)
                    .asFlow()
                    .toList()
            }

            val totalCount = async {
                databaseClient.sql("SELECT count(bank_account_id) as total FROM microservices.bank_accounts WHERE balance BETWEEN :min AND :max")
                    .bind("min", min)
                    .bind("max", max)
                    .fetch()
                    .one()
                    .awaitFirst()
            }

            PageImpl(accountsList.await(), pageable, totalCount.await()["total"] as Long)
                .also { span.tag("PageImpl", it.toString()) }
                .also { log.debug("PageImpl $it") }
        } finally {
            span.end()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankPostgresRepositoryImpl::class.java)
    }
}