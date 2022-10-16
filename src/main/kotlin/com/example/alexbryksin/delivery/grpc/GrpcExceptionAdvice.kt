package com.example.alexbryksin.delivery.grpc

import io.grpc.Status
import io.grpc.StatusException
import net.devh.boot.grpc.server.advice.GrpcAdvice
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler
import org.slf4j.LoggerFactory


@GrpcAdvice
class GrpcExceptionAdvice {

    @GrpcExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): StatusException {
        val status = Status.INTERNAL.withDescription(ex.message).withCause(ex)
        log.error("status: $status")
        return status.asException()
    }

    companion object {
        private val log = LoggerFactory.getLogger(GrpcExceptionAdvice::class.java)
    }
}