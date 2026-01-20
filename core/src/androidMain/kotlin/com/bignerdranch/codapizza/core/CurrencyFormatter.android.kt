package com.bignerdranch.codapizza.core

import android.icu.number.LocalizedNumberFormatter
import android.icu.number.NumberFormatter
import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.icu.util.CurrencyAmount
import android.icu.util.ULocale
import android.os.Build
import android.util.Log

actual fun getCurrencyFormatter(): CurrencyFormatter {
    return formatter
}

private val formatter = object : CurrencyFormatter {
    private val newImpl: LocalizedNumberFormatter by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            NumberFormatter.withLocale(ULocale.US)
        } else {
            Log.e("BLARG", "This line is impossible to get to???")
            TODO("VERSION.SDK_INT < R")
        }
    }

    private val oldImpl: NumberFormat = NumberFormat.getCurrencyInstance(ULocale.US)

    override fun format(price: Double): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val currency = CurrencyAmount(price, Currency.getInstance(ULocale.US))
            newImpl.format(currency).toString()
        } else {
            oldImpl.format(price)
        }
    }
}