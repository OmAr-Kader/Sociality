package com.ramo.sociality.android

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.navigation.replace
import com.ramo.sociality.android.global.navigation.valueOf
import com.ramo.sociality.android.global.navigation.values
import com.ramo.sociality.data.model.PreferenceData
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.data.supaBase.userInfo
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.base.PREF_NAME
import com.ramo.sociality.global.base.PREF_PROFILE_IMAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel(project: Project) : BaseViewModel(project) {

    @Suppress("PropertyName")
    val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    private var prefsJob: kotlinx.coroutines.Job? = null

    fun inti(invoke: List<PreferenceData>.() -> Unit) {
        prefsJob?.cancel()
        prefsJob = launchBack {
            project.pref.prefs {
                _uiState.update { state ->
                    state.copy(preferences = it)
                }
                invoke(it)
            }
        }
    }

    fun findUser(invoke: (UserBase?) -> Unit) {
        launchBack {
            findPrefString(PREF_NAME) { name ->
                findPrefString(PREF_PROFILE_IMAGE) { profileImage ->
                    launchBack {
                        userInfo()?.copy(name = name ?: "", profilePicture = profileImage ?: "")?.let {
                            _uiState.update { state ->
                                state.copy(userBase = it)
                            }
                            invoke(it)
                        } ?: invoke(null)
                    }
                }
            }
        }
    }

    private fun findPrefString(
        key: String,
        value: (it: String?) -> Unit,
    ) {
        if (uiState.value.preferences.isEmpty()) {
            inti {
                value(this@inti.find { it1 -> it1.keyString == key }?.value)
            }
        } else {
            value(uiState.value.preferences.find { it.keyString == key }?.value)
        }
    }

    inline fun <reified T : Screen> findArg() = uiState.value.args.valueOf<T>()

    suspend inline fun <reified T : Screen> writeArguments(screen: T) = kotlinx.coroutines.coroutineScope {
        _uiState.update { state ->
            state.copy(args = state.args.toMutableList().replace(screen), dummy = state.dummy + 1)
        }
    }

    data class State(
        val isProcess: Boolean = false,
        val userBase: UserBase = UserBase(),
        val args: List<Screen> = values(),
        val preferences: List<PreferenceData> = listOf(),
        val dummy: Int = 0,
    )
}