package com.markchiu.cathaysecurities.test.domain.model

object StockSorter {

    fun sort(list: List<StockInfo>, sortType: SortType): List<StockInfo> {
        return when (sortType) {
            SortType.CODE_DESC -> list.sortedByDescending { it.code }
            SortType.CODE_ASC -> list.sortedBy { it.code }
        }
    }
}
