package com.markchiu.cathaysecurities.test.data.remote.dto

import com.google.gson.annotations.SerializedName

// 證交所 OpenAPI：/exchangeReport/STOCK_DAY_AVG_ALL
// 上市個股「日收盤價」與「月平均價」。
// 用於：補月平均價、以及「收盤價 vs 月均價」的紅綠色判斷。
data class StockDayAvgDto(
    @SerializedName("Code") val code: String? = null, // 股票代號
    @SerializedName("Name") val name: String? = null, // 股票名稱
    @SerializedName("ClosingPrice") val closingPrice: String? = null, // 收盤價（此 API 的收盤價，優先用於顏色比較）
    @SerializedName("MonthlyAveragePrice") val monthlyAveragePrice: String? = null, // 月平均價
    @SerializedName("Date") val date: String? = null, // 資料日期
)
