package com.bignerdranch.codapizza.core

import java.text.NumberFormat
import java.util.Locale

actual fun getCurrencyFormatter(): CurrencyFormatter {
    return formatter
}

private val formatter = object : CurrencyFormatter {
    private val currencyFormatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.US)

    override fun format(price: Double): String {
        return currencyFormatter.format(price)
    }
}