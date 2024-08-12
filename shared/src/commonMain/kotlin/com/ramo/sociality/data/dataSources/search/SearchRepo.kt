package com.ramo.sociality.data.dataSources.search

import com.ramo.sociality.data.model.Search

interface SearchRepo {

    suspend fun getSearchesHistory(invoke: suspend (List<Search>) -> Unit)

    suspend fun insertSearch(search: Search): Search?

    suspend fun updateSearch(search: Search, newDate: Long): Search

    suspend fun deleteSearch(search: String): Int

    suspend fun deleteSearchAll(): Int
}