package com.markchiu.cathaysecurities.test.ui.stocklist

import com.markchiu.cathaysecurities.test.domain.model.SortType
import com.markchiu.cathaysecurities.test.domain.model.StockInfo

sealed class StockListUiState {
    data object Loading : StockListUiState()

    data class Success(
        val stocks: List<StockInfo>,
        val sortType: SortType,
        val warningMessage: String? = null,
        val isRefreshing: Boolean = false,
    ) : StockListUiState()

    data class Empty(
        val sortType: SortType,
        val warningMessage: String? = null,
        val isRefreshing: Boolean = false,
    ) : StockListUiState()

    data class Error(
        val message: String,
        val sortType: SortType,
    ) : StockListUiState()
}
