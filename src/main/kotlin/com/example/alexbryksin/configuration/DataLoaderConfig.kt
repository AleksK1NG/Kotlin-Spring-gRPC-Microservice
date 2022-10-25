package com.example.alexbryksin.configuration

import com.example.alexbryksin.domain.BankAccount
import com.example.alexbryksin.domain.Currency
import com.example.alexbryksin.services.BankAccountService
import com.github.javafaker.Faker
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
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

    @Value(value = "\${faker.count:300}")
    val count: Int = 300

    override fun run(vararg args: String?) = runBlocking {

        (0..count).map { _ ->
            async {
                try {
                    bankAccountService.createBankAccount(
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
                } catch (ex: Exception) {
                    log.error("insert mock data error", ex)
                    return@async null
                }
            }
        }.map { it.await() }.forEach { log.info("created bank account: $it") }

        log.info("Mock data successfully inserted")
    }

    companion object {
        private val log = LoggerFactory.getLogger(DataLoaderConfig::class.java)
    }
}