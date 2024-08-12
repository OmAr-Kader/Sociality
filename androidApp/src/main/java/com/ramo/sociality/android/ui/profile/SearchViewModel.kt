package com.ramo.sociality.android.ui.profile

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.SearchData
import com.ramo.sociality.data.model.User
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.util.dateNowMills
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun loadSearchHistory() {
        setProcess()
        launchBack {
            project.search.getSearchesHistory { search ->
                _uiState.update { state ->
                    state.copy(searches = search, isSearchHistory = true, isProcess = false)
                }
            }
        }
    }

    fun onSearchQueryChange(searchText: String) {
        _uiState.update { state ->
            state.copy(searchText = searchText)
        }
    }

    fun doSearch(searchText: String) {
        setProcess()
        launchBack {
            project.profile.fetchProfilesOnName(searchText).also { usersOnSearch ->
                SearchData(searchText = searchText, date = dateNowMills).also { search ->
                    project.search.updateSearch(search, search.date).also {
                        _uiState.update { state ->
                            state.copy(users = usersOnSearch, isSearchHistory = false, isProcess = false)
                        }
                    }
                }
            }
        }
    }

    private fun setProcess() {
        _uiState.update { state ->
            state.copy(isProcess = true)
        }
    }

    data class State(
        val searchText: String = "",
        val users: List<User> = emptyList(),
        val searches: List<SearchData> = emptyList(),
        val isSearchHistory: Boolean = true,
        val isProcess: Boolean = false,
    )
}