package com.example.alexbryksin

import com.example.grpc.bank.service.BankAccount
import com.example.grpc.bank.service.BankAccount.CreateBankAccountRequest
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

//@SpringBootTest
class KotlinSpringGrpcApplicationTests {

    @Test
    fun createBankAccount(): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 8000).usePlaintext().build()

        try {
            val client = BankAccountServiceGrpcKt.BankAccountServiceCoroutineStub(channel)
            val request = CreateBankAccountRequest.newBuilder()
                .setEmail("alexander.bryksin@yandex.ru")
                .build()
            val response = client.createBankAccount(request)

            println("response: $response")
        } catch (ex: Exception) {
            println("ex: $ex")
        } finally {
            channel.shutdown()
            channel.awaitTermination(5000, TimeUnit.MILLISECONDS)
        }

    }

    @Test
    fun findAllByAmount(): Unit = runBlocking {
        val channel = ManagedChannelBuilder.forAddress("localhost", 8000).usePlaintext().build()

        try {
            val client = BankAccountServiceGrpcKt.BankAccountServiceCoroutineStub(channel)
            val request = BankAccount.GetAllByBalanceRequest.newBuilder()
                .setMin(0.0)
                .setMax(500000.00)
                .setPage(1)
                .setSize(20)
                .build()
            val response = client.getAllByBalance(request)

            response.collectIndexed { index, value -> println("index: $index, value: $value") }
        } catch (ex: Exception) {
            println("ex: $ex")
        } finally {
            channel.shutdown()
            channel.awaitTermination(5000, TimeUnit.MILLISECONDS)
        }
    }

}
