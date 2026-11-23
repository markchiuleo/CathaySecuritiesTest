package com.markchiu.cathaysecurities.test.data.repository

import com.markchiu.cathaysecurities.test.data.remote.TwseApiService
import com.markchiu.cathaysecurities.test.domain.merger.StockDataMerger
import com.markchiu.cathaysecurities.test.domain.model.StockInfo
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

data class StockLoadResult(
    val stocks: List<StockInfo>,
    val dayFailed: Boolean,
    val avgFailed: Boolean,
    val bwibbuFailed: Boolean,
) {
    val allFailed: Boolean get() = dayFailed && avgFailed && bwibbuFailed
    val anyFailed: Boolean get() = dayFailed || avgFailed || bwibbuFailed
    val isEmpty: Boolean get() = stocks.isEmpty()
}

class StockRepository(
    private val api: TwseApiService,
) {

    suspend fun loadStocks(): StockLoadResult = coroutineScope {
        val dayDeferred = async { runCatching { api.getStockDayAll() } }
        val avgDeferred = async { runCatching { api.getStockDayAvgAll() } }
        val bwDeferred = async { runCatching { api.getBwibbuAll() } }

        val dayResult = dayDeferred.await()
        val avgResult = avgDeferred.await()
        val bwResult = bwDeferred.await()

        val dayFailed = dayResult.isFailure
        val avgFailed = avgResult.isFailure
        val bwFailed = bwResult.isFailure

        val stocks = StockDataMerger.merge(
            dayList = dayResult.getOrNull(),
            avgList = avgResult.getOrNull(),
            bwibbuList = bwResult.getOrNull(),
        )

        StockLoadResult(
            stocks = stocks,
            dayFailed = dayFailed,
            avgFailed = avgFailed,
            bwibbuFailed = bwFailed,
        )
    }
}
