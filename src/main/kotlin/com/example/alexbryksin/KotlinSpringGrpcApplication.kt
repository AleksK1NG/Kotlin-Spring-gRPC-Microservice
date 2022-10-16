package com.example.alexbryksin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringGrpcApplication

fun main(args: Array<String>) {
	runApplication<KotlinSpringGrpcApplication>(*args)
}
