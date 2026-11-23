package com.markchiu.cathaysecurities.test.data.remote.dto

import com.google.gson.annotations.SerializedName

// 證交所 OpenAPI：/exchangeReport/STOCK_DAY_ALL
// 上市個股「日成交」資訊（牌卡多數價格／成交量欄位由此而來）。
// 數值欄位 API 以字串回傳，後續再經 NumberParser 轉成 Double／Long。
data class StockDayDto(
    @SerializedName("Code") val code: String? = null, // 股票代號，例如 2330
    @SerializedName("Name") val name: String? = null, // 股票名稱，例如 台積電
    @SerializedName("TradeVolume") val tradeVolume: String? = null, // 成交股數（成交量）
    @SerializedName("TradeValue") val tradeValue: String? = null, // 成交金額
    @SerializedName("OpeningPrice") val openingPrice: String? = null, // 開盤價
    @SerializedName("HighestPrice") val highestPrice: String? = null, // 最高價
    @SerializedName("LowestPrice") val lowestPrice: String? = null, // 最低價
    @SerializedName("ClosingPrice") val closingPrice: String? = null, // 收盤價
    @SerializedName("Change") val change: String? = null, // 漲跌價差（相對前一交易日）
    @SerializedName("Transaction") val transaction: String? = null, // 成交筆數
    @SerializedName("Date") val date: String? = null, // 資料日期
)
