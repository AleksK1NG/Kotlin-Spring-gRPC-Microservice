package com.example.alexbryksin.configuration

import com.github.javafaker.Faker
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*


@Configuration
class FakerConfig {
    @Value(value = "\${faker.locale:en}")
    val locale: String = "en"

    @Bean
    fun faker(): Faker = Faker(Locale(locale))
}