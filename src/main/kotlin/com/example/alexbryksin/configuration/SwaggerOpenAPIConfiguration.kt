package com.example.alexbryksin.configuration

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Contact
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration


@OpenAPIDefinition(
    info = Info(
        title = "Kotlin, Spring WebFlux, gRPC, PostgreSQL Microservice",
        description = "Kotlin, Spring WebFlux, gRPC, PostgreSQL Microservice example",
        contact = Contact(
            name = "Alexander Bryksin",
            email = "alexander.bryksin@yandex.ru",
            url = "https://github.com/AleksK1NG"
        )
    )
)
@Configuration
class SwaggerOpenAPIConfiguration
