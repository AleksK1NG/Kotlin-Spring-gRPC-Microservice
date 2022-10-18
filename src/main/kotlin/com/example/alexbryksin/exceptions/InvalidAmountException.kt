package com.example.alexbryksin.exceptions

class InvalidAmountException : RuntimeException {
    constructor(amount: String?) : super("invalid amount $amount")
    constructor(amount: String?, cause: Throwable?) : super("invalid amount $amount", cause)
    constructor(cause: Throwable?) : super(cause)
}