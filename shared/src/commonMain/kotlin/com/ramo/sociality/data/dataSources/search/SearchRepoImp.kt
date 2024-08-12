package com.ramo.sociality.data.dataSources.search

import com.ramo.sociality.data.model.Search
import com.ramo.sociality.data.util.REALM_FAILED
import com.ramo.sociality.data.util.REALM_SUCCESS
import com.ramo.sociality.global.base.RealmLocal
import io.realm.kotlin.ext.query
import io.realm.kotlin.query.Sort

class SearchRepoImp(private val realm: RealmLocal) : SearchRepo {

    override suspend fun getSearchesHistory(invoke: suspend (List<Search>) -> Unit) {
        return kotlinx.coroutines.coroutineScope {
            kotlin.runCatching {
                realm.query(Search::class).sort("date", Sort.DESCENDING).find()
            }.getOrNull()?.let {
                invoke.invoke(it)
            } ?: invoke.invoke(emptyList())
        }
    }

    override suspend fun insertSearch(search: Search): Search? {
        return realm.write {
            try {
                copyToRealm(search, io.realm.kotlin.UpdatePolicy.ALL)
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun updateSearch(search: Search, newDate: Long): Search {
        return realm.write {
            return@write query<Search>("searchText == $0", search.searchText).first().find()?.also {
                it.date = newDate
            }
        }?.let {
            return it
        } ?: realm.write {
            kotlin.run {
                copyToRealm(search, io.realm.kotlin.UpdatePolicy.ALL)
                search
            }
        }
    }

    override suspend fun deleteSearch(search: String): Int {
        return realm.write {
            try {
                query<Search>("searchText == $0", search).first().find()?.let {
                    delete(it)
                }
            } catch (e: Exception) {
                return@write null
            }
        }.let {
            return@let if (it == null) REALM_FAILED else REALM_SUCCESS
        }
    }

    override suspend fun deleteSearchAll(): Int {
        return realm.write {
            kotlin.runCatching {
                delete(schemaClass = Search::class)
            }.getOrNull()
        }.let {
            return@let if (it == null) REALM_FAILED else REALM_SUCCESS
        }
    }
}