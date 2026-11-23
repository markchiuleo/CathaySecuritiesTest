package com.markchiu.cathaysecurities.test.ui.stocklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.markchiu.cathaysecurities.test.data.repository.StockLoadResult
import com.markchiu.cathaysecurities.test.data.repository.StockRepository
import com.markchiu.cathaysecurities.test.domain.model.SortType
import com.markchiu.cathaysecurities.test.domain.model.StockInfo
import com.markchiu.cathaysecurities.test.domain.model.StockSorter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class StockListViewModel(
    private val repository: StockRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StockListUiState>(StockListUiState.Loading)
    val uiState: StateFlow<StockListUiState> = _uiState.asStateFlow()

    private var cachedStocks: List<StockInfo> = emptyList()
    private var currentSortType: SortType = SortType.CODE_DESC
    private var lastWarning: String? = null
    private var loadJob: Job? = null

    init {
        loadStocks()
    }

    fun loadStocks() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            val hasCache = cachedStocks.isNotEmpty()
            if (hasCache) {
                // 下拉重整：保留現有列表，只顯示 refresh indicator
                when (val current = _uiState.value) {
                    is StockListUiState.Success ->
                        _uiState.value = current.copy(isRefreshing = true)
                    is StockListUiState.Empty ->
                        _uiState.value = current.copy(isRefreshing = true)
                    else -> Unit
                }
            } else {
                _uiState.value = StockListUiState.Loading
            }

            try {
                val result = repository.loadStocks()
                handleResult(result)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                if (hasCache) {
                    // 重整失敗仍保留舊資料，並提示
                    publishSorted(
                        warningOverride = "資料更新失敗，已顯示先前資料，請稍後再試",
                    )
                } else {
                    _uiState.value = StockListUiState.Error(
                        message = "資料載入失敗，請檢查網路後重試",
                        sortType = currentSortType,
                    )
                }
            }
        }
    }

    fun setSortType(sortType: SortType) {
        currentSortType = sortType
        // 即使選同一選項也重新發布，確保 UI sortType 與列表狀態一致
        publishSorted()
    }

    private fun handleResult(result: StockLoadResult) {
        lastWarning = buildWarning(result)
        if (result.allFailed || (result.dayFailed && result.stocks.isEmpty() && result.avgFailed)) {
            if (cachedStocks.isNotEmpty()) {
                publishSorted(
                    warningOverride = "資料更新失敗，已顯示先前資料，請稍後再試",
                )
                return
            }
            _uiState.value = StockListUiState.Error(
                message = "無法取得股票資料，請稍後再試",
                sortType = currentSortType,
            )
            return
        }
        if (result.stocks.isEmpty() && cachedStocks.isEmpty()) {
            _uiState.value = StockListUiState.Empty(
                sortType = currentSortType,
                warningMessage = lastWarning,
            )
            return
        }
        if (result.stocks.isNotEmpty()) {
            cachedStocks = result.stocks
        }
        publishSorted()
    }

    private fun publishSorted(warningOverride: String? = null) {
        val warning = warningOverride ?: lastWarning
        if (warningOverride != null) {
            lastWarning = warningOverride
        }
        val sorted = StockSorter.sort(cachedStocks, currentSortType)
        _uiState.value = if (sorted.isEmpty()) {
            StockListUiState.Empty(
                sortType = currentSortType,
                warningMessage = warning,
                isRefreshing = false,
            )
        } else {
            StockListUiState.Success(
                stocks = sorted,
                sortType = currentSortType,
                warningMessage = warning,
                isRefreshing = false,
            )
        }
    }

    private fun buildWarning(result: StockLoadResult): String? {
        if (!result.anyFailed) return null
        val parts = buildList {
            if (result.dayFailed) add("日成交")
            if (result.avgFailed) add("收盤／月均")
            if (result.bwibbuFailed) add("本益比等")
        }
        return if (parts.isEmpty()) {
            null
        } else {
            "部分資料來源失敗（${parts.joinToString("、")}），已顯示可取得的欄位"
        }
    }

    class Factory(
        private val repository: StockRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StockListViewModel::class.java)) {
                return StockListViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
