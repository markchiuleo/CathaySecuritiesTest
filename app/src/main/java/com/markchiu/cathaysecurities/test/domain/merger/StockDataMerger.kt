package com.markchiu.cathaysecurities.test.domain.merger

import com.markchiu.cathaysecurities.test.data.remote.dto.BwibbuDto
import com.markchiu.cathaysecurities.test.data.remote.dto.StockDayAvgDto
import com.markchiu.cathaysecurities.test.data.remote.dto.StockDayDto
import com.markchiu.cathaysecurities.test.domain.model.StockInfo
import com.markchiu.cathaysecurities.test.util.NumberParser

// 用 Code 當 key 合併三支 API，不靠陣列順序
// 列表主體以 STOCK_DAY_ALL 為準；AVG 補月均／比色；BWIBBU 補本益比等
object StockDataMerger {

    fun merge(
        dayList: List<StockDayDto>?,
        avgList: List<StockDayAvgDto>?,
        bwibbuList: List<BwibbuDto>?,
    ): List<StockInfo> {
        val avgMap = avgList
            .orEmpty()
            .mapNotNull { dto -> dto.code?.trim()?.takeIf { it.isNotEmpty() }?.let { it to dto } }
            .toMap()

        val bwibbuMap = bwibbuList
            .orEmpty()
            .mapNotNull { dto -> dto.code?.trim()?.takeIf { it.isNotEmpty() }?.let { it to dto } }
            .toMap()

        val days = dayList.orEmpty()
        if (days.isEmpty()) {
            // DAY 沒資料時，至少用 AVG 撐出列表（成交欄位會是空的）
            if (avgMap.isEmpty()) return emptyList()
            return avgMap.values.map { avg ->
                val code = avg.code!!.trim()
                val bw = bwibbuMap[code]
                toStockInfo(
                    code = code,
                    name = avg.name?.trim().orEmpty().ifEmpty { bw?.name?.trim().orEmpty() },
                    day = null,
                    avg = avg,
                    bw = bw,
                )
            }
        }

        return days.mapNotNull { day ->
            val code = day.code?.trim()?.takeIf { it.isNotEmpty() } ?: return@mapNotNull null
            val avg = avgMap[code]
            val bw = bwibbuMap[code]
            toStockInfo(
                code = code,
                name = day.name?.trim()
                    .orEmpty()
                    .ifEmpty { avg?.name?.trim().orEmpty() }
                    .ifEmpty { bw?.name?.trim().orEmpty() },
                day = day,
                avg = avg,
                bw = bw,
            )
        }
    }

    private fun toStockInfo(
        code: String,
        name: String,
        day: StockDayDto?,
        avg: StockDayAvgDto?,
        bw: BwibbuDto?,
    ): StockInfo {
        val dayClosing = NumberParser.parseDouble(day?.closingPrice)
        val avgClosing = NumberParser.parseDouble(avg?.closingPrice)
        return StockInfo(
            code = code,
            name = name,
            openingPrice = NumberParser.parseDouble(day?.openingPrice),
            highestPrice = NumberParser.parseDouble(day?.highestPrice),
            lowestPrice = NumberParser.parseDouble(day?.lowestPrice),
            // 顯示收盤價優先 AVG，與「收盤 vs 月均」顏色判斷同一組
            closingPrice = avgClosing ?: dayClosing,
            avgClosingPrice = avgClosing,
            monthlyAveragePrice = NumberParser.parseDouble(avg?.monthlyAveragePrice),
            change = NumberParser.parseDouble(day?.change),
            tradeVolume = NumberParser.parseLong(day?.tradeVolume),
            tradeValue = NumberParser.parseLong(day?.tradeValue),
            transaction = NumberParser.parseLong(day?.transaction),
            peRatio = NumberParser.parseDouble(bw?.peRatio),
            dividendYield = NumberParser.parseDouble(bw?.dividendYield),
            pbRatio = NumberParser.parseDouble(bw?.pbRatio),
        )
    }
}
