package com.example.alexbryksin.domain

import com.example.alexbryksin.exceptions.InvalidAmountException
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*


@Table(schema = "microservices", name = "bank_accounts")
data class BankAccount(
    @Column("bank_account_id") @Id var id: UUID?,
    @Column("email") var email: String = "",
    @Column("first_name") var firstName: String = "",
    @Column("last_name") var lastName: String = "",
    @Column("address") var address: String = "",
    @Column("phone") var phone: String = "",
    @Column("currency") var currency: Currency = Currency.USD,
    @Column("balance") var balance: BigDecimal = BigDecimal.ZERO,
    @Column("created_at") var createdAt: LocalDateTime? = null,
    @Column("updated_at") var updatedAt: LocalDateTime? = null,
) {
    companion object

    fun depositAmount(amount: BigDecimal): BankAccount {
        if (amount < BigDecimal.ZERO) throw InvalidAmountException(amount.toString())
        return this.apply {
            balance = balance.plus(amount)
            updatedAt = LocalDateTime.now()
        }
    }

    fun withdrawAmount(amount: BigDecimal): BankAccount {
        if (balance.minus(amount) < BigDecimal.ZERO) throw InvalidAmountException(amount.toString())
        return this.apply {
            balance = balance.minus(amount)
            updatedAt = LocalDateTime.now()
        }
    }
}

