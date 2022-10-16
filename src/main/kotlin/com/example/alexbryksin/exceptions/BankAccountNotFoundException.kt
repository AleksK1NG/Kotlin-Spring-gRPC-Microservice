package com.example.alexbryksin.exceptions

class BankAccountNotFoundException : RuntimeException {
    constructor() : super()
    constructor(id: String?) : super("bank account with $id not found")
    constructor(id: String?, cause: Throwable?) : super("bank account with $id not found", cause)
}