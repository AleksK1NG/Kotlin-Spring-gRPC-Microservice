package com.example.alexbryksin.utils

import org.springframework.cloud.sleuth.ScopedSpan
import org.springframework.cloud.sleuth.Tracer


inline fun <T, R> T.runWithTracing(span: ScopedSpan, block: T.() -> R): R {
    return try {
        block()
    } catch (ex: Exception) {
        span.error(ex)
        throw ex
    } finally {
        span.end()
    }
}

inline fun <T, R> T.runWithTracing(tracer: Tracer, name: String, block: T.() -> R): R {
    val span = tracer.startScopedSpan(name)

    return try {
        block()
    } catch (ex: Exception) {
        span.error(ex)
        throw ex
    } finally {
        span.end()
    }
}