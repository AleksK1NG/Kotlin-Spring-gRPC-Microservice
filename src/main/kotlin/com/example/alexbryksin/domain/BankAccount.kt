package com.example.alexbryksin.domain

import com.example.alexbryksin.dto.CreateBankAccountDto
import com.example.alexbryksin.dto.SuccessBankAccountResponse
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
    @Column(BANK_ACCOUNT_ID) @Id var id: UUID?,
    @get:Email @Column(EMAIL) var email: String = "",
    @get:Size(min = 3, max = 60) @Column(FIRST_NAME) var firstName: String = "",
    @get:Size(min = 3, max = 60) @Column(LAST_NAME) var lastName: String = "",
    @get:Size(min = 3, max = 500) @Column(ADDRESS) var address: String = "",
    @get:Size(min = 6, max = 20) @Column(PHONE) var phone: String = "",
    @Column(CURRENCY) var currency: Currency = Currency.USD,
    @get:DecimalMin(value = "0.0") @Column(BALANCE) var balance: BigDecimal = BigDecimal.ZERO,
    @Column(CREATED_AT) var createdAt: LocalDateTime? = null,
    @Column(UPDATED_AT) var updatedAt: LocalDateTime? = null,
) {

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

    companion object {
        const val BANK_ACCOUNT_ID = "bank_account_id"
        const val EMAIL = "email"
        const val FIRST_NAME = "first_name"
        const val LAST_NAME = "last_name"
        const val ADDRESS = "address"
        const val PHONE = "phone"
        const val BALANCE = "balance"
        const val CURRENCY = "currency"
        const val CREATED_AT = "created_at"
        const val UPDATED_AT = "updated_at"
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

fun BankAccount.toSuccessHttpResponse(): SuccessBankAccountResponse {
    return SuccessBankAccountResponse(
        id = this.id,
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        address = this.address,
        phone = this.phone,
        currency = this.currency,
        balance = this.balance,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
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

fun BankAccount.Companion.of(request: CreateBankAccountDto): BankAccount {
    return BankAccount(
        id = null,
        email = request.email,
        firstName = request.firstName,
        lastName = request.lastName,
        address = request.address,
        phone = request.phone,
        currency = request.currency,
        balance = request.balance,
        updatedAt = LocalDateTime.now(),
        createdAt = LocalDateTime.now(),
    )
}