package com.example.alexbryksin

import com.example.grpc.bank.service.BankAccount.CreateBankAccountRequest
import com.example.grpc.bank.service.BankAccountServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

//@SpringBootTest
class KotlinSpringGrpcApplicationTests {

	@Test
	fun createBankAccount(): Unit = runBlocking {
		val channel = ManagedChannelBuilder.forAddress("localhost", 8000).usePlaintext().build()
		val client = BankAccountServiceGrpcKt.BankAccountServiceCoroutineStub(channel)
		val request = CreateBankAccountRequest.newBuilder()
			.setEmail("alexander.bryksin@yandex.ru")
			.build()
		val response = client.createBankAccount(request)

		println("response: $response")
	}

}
