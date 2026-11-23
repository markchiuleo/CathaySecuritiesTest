package com.markchiu.cathaysecurities.test.data.remote.dto

import com.google.gson.annotations.SerializedName

// 證交所 OpenAPI：/exchangeReport/BWIBBU_ALL
// 上市個股「本益比、殖利率、股價淨值比」。
// 點擊牌卡跳 Alert 時顯示的三個欄位由此而來。
data class BwibbuDto(
    @SerializedName("Code") val code: String? = null, // 股票代號
    @SerializedName("Name") val name: String? = null, // 股票名稱
    @SerializedName("PEratio") val peRatio: String? = null, // 本益比
    @SerializedName("DividendYield") val dividendYield: String? = null, // 殖利率(%)
    @SerializedName("PBratio") val pbRatio: String? = null, // 股價淨值比
    @SerializedName("Date") val date: String? = null, // 資料日期
)
