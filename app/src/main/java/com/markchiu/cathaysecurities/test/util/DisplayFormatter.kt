package com.markchiu.cathaysecurities.test.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object DisplayFormatter {

    private const val PLACEHOLDER = "--"

    private val decimalSymbols = DecimalFormatSymbols(Locale.US).apply {
        decimalSeparator = '.'
        groupingSeparator = ','
    }

    private val priceFormat = DecimalFormat("#,##0.####", decimalSymbols)
    private val longFormat = DecimalFormat("#,##0", decimalSymbols)

    fun formatNumber(value: Double?): String {
        if (value == null) return PLACEHOLDER
        return priceFormat.format(value)
    }

    fun formatLong(value: Long?): String {
        if (value == null) return PLACEHOLDER
        return longFormat.format(value)
    }

    fun placeholder(): String = PLACEHOLDER
}
