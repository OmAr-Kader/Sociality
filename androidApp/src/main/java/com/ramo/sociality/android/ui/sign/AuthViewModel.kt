package com.ramo.sociality.android.ui.sign

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.PreferenceData
import com.ramo.sociality.data.model.User
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.data.supaBase.registerAuth
import com.ramo.sociality.data.supaBase.signInAuth
import com.ramo.sociality.data.supaBase.userInfo
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.base.PREF_NAME
import com.ramo.sociality.global.base.PREF_PROFILE_IMAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AuthViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun setName(it: String) {
        _uiState.update { state ->
            state.copy(name = it)
        }
    }

    fun setEmail(it: String) {
        _uiState.update { state ->
            state.copy(email = it)
        }
    }

    fun setPassword(it: String) {
        _uiState.update { state ->
            state.copy(password = it)
        }
    }

    fun toggleScreen() {
        _uiState.update { state ->
            state.copy(isLoginScreen = !state.isLoginScreen)
        }
    }

    fun createNewUser(invoke: () -> Unit, failed: () -> Unit) {
        uiState.value.let { state ->
            setIsProcess(true)
            launchBack {
                registerAuth(
                    UserBase(
                        username = state.email.split(regex = Regex("@")).firstOrNull() ?: state.email,
                        email = state.email,
                        name = state.name
                    ), state.password, invoke = {
                        doSignUp(state, invoke, failed)
                    },
                ) {
                    setIsProcess(false)
                    failed()
                }
            }
        }
    }

    private suspend fun doSignUp(state: State, invoke: () -> Unit, failed: () -> Unit) {
        //signInAuth(state.email, state.password, invoke = {
        userInfo()?.let { userBase ->
            project.profile.addNewUser(
                User(
                    userId = userBase.id,
                    name = state.name,
                    email = userBase.email,
                    username = userBase.username
                )
            )?.let { user ->
                project.friendship.addNewFriendship(Friendship(userId = user.userId, friends = arrayOf())).let {
                    project.pref.updatePref(PreferenceData(PREF_NAME, state.name), state.name).also {
                        project.pref.updatePref(PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture), user.profilePicture).also {
                            setIsProcess(false)
                            invoke()
                        }
                    }
                }
            } ?: kotlin.run {
                setIsProcess(false)
                invoke()
            }
        } ?: kotlin.run {
            setIsProcess(false)
            failed()
        }
    }

    private suspend fun doLogin(state: State, invoke: () -> Unit, failed: () -> Unit) {
        signInAuth(state.email, state.password, invoke = {
            userInfo()?.let {
                project.profile.getProfileOnUserId(it.id)?.also { user ->
                    project.pref.updatePref(PreferenceData(PREF_NAME, state.name), state.name).also {
                        project.pref.updatePref(PreferenceData(PREF_PROFILE_IMAGE, user.profilePicture), user.profilePicture).also {
                            setIsProcess(false)
                            invoke()
                        }
                    }
                } ?: kotlin.run {
                    setIsProcess(false)
                    invoke()
                }
            } ?: kotlin.run {
                setIsProcess(false)
                failed()
            }
        }, {
            failed()
        })
    }

    fun loginUser(invoke: () -> Unit, failed: () -> Unit) {
        uiState.value.let { state ->
            setIsProcess(true)
            launchBack {
                doLogin(state, invoke, failed)
            }
        }
    }

    private fun setIsProcess(it: Boolean) {
        _uiState.update { state ->
            state.copy(isProcess = it)
        }
    }

    data class State(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val isLoginScreen: Boolean = false,
        val isProcess: Boolean = false,
        val isErrorPressed: Boolean = false,
    )

}