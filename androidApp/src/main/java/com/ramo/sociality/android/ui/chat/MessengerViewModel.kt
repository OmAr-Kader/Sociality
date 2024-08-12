package com.ramo.sociality.android.ui.chat

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.util.injectChatForChatScreen
import com.ramo.sociality.data.util.injectChats
import com.ramo.sociality.di.Project
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MessengerViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun loadChat(userId: String) {
        launchBack {
            project.chat.getChatsOnUser(listOf(userId)).also { list ->
                project.profile.getAllProfilesOnUserIds(list.map { it.members.toList() }.flatten()).also { users ->
                    list.map {
                        it.injectChatForChatScreen(userId, users)
                    }.also { chats ->
                        _uiState.update { state ->
                            state.copy(chats = chats, isProcess = false)
                        }
                        fetchRealTimeMessages(chatIds = chats.map { it.id }, userId)
                    }
                }
            }
        }
    }

    private fun fetchRealTimeMessages(chatIds: List<Long>, userId: String) {
        launchBack {
            project.message.fetchRealTimeMessagesOfChats(chatIds) { messages ->
                launchBack {
                    uiState.value.chats.injectChats(messages, userId).also { newChats ->
                        _uiState.update { state ->
                            state.copy(chats = newChats, dummy = state.dummy + 1, isProcess = false)
                        }
                    }
                }
            }
        }
    }

    data class State(
        val chats: List<Chat> = emptyList(),
        val dummy: Int = 0,
        val isProcess: Boolean = true,
    )

}