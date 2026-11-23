package com.markchiu.cathaysecurities.test.domain.model

object PriceColorLogic {

    // 收盤價高於月平均 → 紅；低於 → 綠；相等或任一無效 → 一般色
    fun closingVsMonthly(closing: Double?, monthlyAverage: Double?): PriceColor {
        if (closing == null || monthlyAverage == null) return PriceColor.NEUTRAL
        return when {
            closing > monthlyAverage -> PriceColor.UP
            closing < monthlyAverage -> PriceColor.DOWN
            else -> PriceColor.NEUTRAL
        }
    }

    // 漲跌價差：正紅、負綠、0／無效一般色。依數值判斷，不要看字串有沒有 '+'
    fun changeColor(change: Double?): PriceColor {
        if (change == null) return PriceColor.NEUTRAL
        return when {
            change > 0.0 -> PriceColor.UP
            change < 0.0 -> PriceColor.DOWN
            else -> PriceColor.NEUTRAL
        }
    }
}
