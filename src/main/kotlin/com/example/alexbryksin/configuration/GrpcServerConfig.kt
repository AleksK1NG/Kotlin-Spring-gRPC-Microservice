package com.example.alexbryksin.configuration

import net.devh.boot.grpc.server.event.GrpcServerStartedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener


@Configuration
class GrpcServerConfig {
    @EventListener
    fun onServerStarted(event: GrpcServerStartedEvent) {
        log.info("gRPC Server started, services: ${event.server.services[0].methods}")
    }

    companion object {
        private val log = LoggerFactory.getLogger(GrpcServerConfig::class.java)
    }
}