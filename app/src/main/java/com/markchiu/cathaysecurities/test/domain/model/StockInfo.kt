package com.markchiu.cathaysecurities.test.domain.model

// 三支 API 合併後給 UI 用的資料；缺值用 null，畫面顯示成 --
data class StockInfo(
    val code: String,
    val name: String,
    val openingPrice: Double?,
    val highestPrice: Double?,
    val lowestPrice: Double?,
    // 畫面收盤價：AVG 有就用 AVG，否則用 DAY
    val closingPrice: Double?,
    // 專門跟月均比顏色用（來自 AVG）
    val avgClosingPrice: Double?,
    val monthlyAveragePrice: Double?,
    val change: Double?,
    val tradeVolume: Long?,
    val tradeValue: Long?,
    val transaction: Long?,
    val peRatio: Double?,
    val dividendYield: Double?,
    val pbRatio: Double?,
) {
    val closingPriceColor: PriceColor
        get() = PriceColorLogic.closingVsMonthly(avgClosingPrice, monthlyAveragePrice)

    val changeColor: PriceColor
        get() = PriceColorLogic.changeColor(change)
}
