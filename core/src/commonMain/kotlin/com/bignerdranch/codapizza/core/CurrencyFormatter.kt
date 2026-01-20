package com.bignerdranch.codapizza.core

interface CurrencyFormatter {
    fun format(price: Double): String
}

expect fun getCurrencyFormatter(): CurrencyFormatter