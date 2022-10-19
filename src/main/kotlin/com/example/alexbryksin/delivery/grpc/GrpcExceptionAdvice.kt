package com.example.alexbryksin.delivery.grpc

import com.example.alexbryksin.exceptions.BankAccountNotFoundException
import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.LoggerFactory
import org.springframework.web.bind.MethodArgumentNotValidException
import javax.validation.ConstraintViolationException


@GrpcAdvice
class GrpcExceptionAdvice {

    @GrpcExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): StatusException {
        val status = Status.INTERNAL.withDescription(ex.message).withCause(ex)
        log.error("status: $status")
        return status.asException()
    }

    @GrpcExceptionHandler(BankAccountNotFoundException::class)
    fun handleBankAccountNotFoundException(ex: BankAccountNotFoundException): StatusException {
        val status = Status.INVALID_ARGUMENT.withDescription(ex.message).withCause(ex)
        log.error("status: $status")
        return status.asException()
    }

    @GrpcExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): StatusException {
        val errorMap: MutableMap<String, String> = HashMap()
        ex.bindingResult.fieldErrors.forEach { error -> error.defaultMessage?.let { errorMap[error.field] = it } }
        val status = Status.INVALID_ARGUMENT.withDescription(errorMap.toString()).withCause(ex)
        log.error("status: $status")
        return status.asException()
    }

    @GrpcExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): StatusException {
        val status = Status.INVALID_ARGUMENT.withDescription(ex.toString()).withCause(ex)
        log.error("status: $status")
        return status.asException()
    }



    companion object {
        private val log = LoggerFactory.getLogger(GrpcExceptionAdvice::class.java)
    }
}