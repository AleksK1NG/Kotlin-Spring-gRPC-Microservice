package com.example.alexbryksin.domain

import com.example.alexbryksin.exceptions.InvalidAmountException
import com.example.grpc.bank.service.BankAccount.BankAccountData
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.Email
import javax.validation.constraints.Size


@Table(schema = "microservices", name = "bank_accounts")
data class BankAccount(
    @Column("bank_account_id") @Id var id: UUID?,
    @get:Email @Column("email") var email: String = "",
    @get:Size(min = 3, max = 60) @Column("first_name") var firstName: String = "",
    @get:Size(min = 3, max = 60) @Column("last_name") var lastName: String = "",
    @get:Size(min = 3, max = 500) @Column("address") var address: String = "",
    @get:Size(min = 6, max = 20) @Column("phone") var phone: String = "",
    @Column("currency") var currency: Currency = Currency.USD,
    @get:DecimalMin(value = "0.0") @Column("balance") var balance: BigDecimal = BigDecimal.ZERO,
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

fun BankAccount.toProto(): BankAccountData {
    return BankAccountData.newBuilder()
        .setId(this.id.toString())
        .setEmail(this.email)
        .setFirstName(this.firstName)
        .setLastName(this.lastName)
        .setAddress(this.address)
        .setPhone(this.phone)
        .setBalance(this.balance.toDouble())
        .setCurrency(this.currency.name)
        .setUpdatedAt(this.updatedAt.toString())
        .setCreatedAt(this.createdAt.toString())
        .build()
}

fun BankAccount.Companion.of(request: com.example.grpc.bank.service.BankAccount.CreateBankAccountRequest): BankAccount {
    return BankAccount(
        id = null,
        email = request.email,
        firstName = request.firstName,
        lastName = request.lastName,
        address = request.address,
        phone = request.phone,
        currency = Currency.valueOf(request.currency),
        balance = BigDecimal.valueOf(request.balance),
        updatedAt = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
    )
}