package com.ramo.sociality.data.dataSources.search

import com.ramo.sociality.data.model.Search
import com.ramo.sociality.data.model.SearchData
import com.ramo.sociality.data.util.toSearchDate

class SearchBase(
    private val repo: SearchRepo
) {

    suspend fun getSearchesHistory(invoke: (List<SearchData>) -> Unit) = repo.getSearchesHistory {
        invoke(it.toSearchDate())
    }

    suspend fun insertSearch(search: SearchData): SearchData? = repo.insertSearch(Search(search))?.let { SearchData(it) }

    suspend fun updateSearch(search: SearchData, newDate: Long): SearchData = SearchData(repo.updateSearch(Search(search), newDate))

    suspend fun deleteSearch(search: String): Int = repo.deleteSearch(search)

    suspend fun deleteSearchAll(): Int = repo.deleteSearchAll()
}