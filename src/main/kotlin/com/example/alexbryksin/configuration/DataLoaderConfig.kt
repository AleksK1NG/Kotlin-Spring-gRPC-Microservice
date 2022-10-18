package com.example.alexbryksin.configuration

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.Currency
import com.example.alexbryksin.service.BankAccountService
import com.github.javafaker.Faker
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import java.math.BigDecimal
import java.time.LocalDateTime


@Configuration
@ConditionalOnProperty(prefix = "faker", name = ["enable"])
class DataLoaderConfig(
    private val bankAccountService: BankAccountService,
    private val faker: Faker,
) : CommandLineRunner {

    override fun run(vararg args: String?) = runBlocking {

        try {
            (0..100).forEach { _ ->
                val createdBankAccount = bankAccountService.createBankAccount(
                    BankAccount(
                        id = null,
                        email = faker.internet().emailAddress(),
                        firstName = faker.name().firstName(),
                        lastName = faker.name().lastName(),
                        address = faker.address().fullAddress(),
                        phone = faker.phoneNumber().cellPhone(),
                        currency = Currency.USD,
                        balance = BigDecimal.valueOf(faker.number().numberBetween(0, 500000).toDouble()),
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                )
                log.info("created bank account: $createdBankAccount")
            }
            log.info("Mock data successfully inserted")
        } catch (ex: Exception) {
            log.error("insert mock data error", ex)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(DataLoaderConfig::class.java)
    }
}