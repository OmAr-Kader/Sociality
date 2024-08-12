package com.ramo.sociality.android.ui.chat

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.Message
import com.ramo.sociality.data.util.injectChatForChatScreen
import com.ramo.sociality.data.util.injectChatMessages
import com.ramo.sociality.data.util.injectChatMessagesNormal
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.util.dateNow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChatViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun loadChat(userId: String, chatRoute: Screen.ChatRoute) {
        if (chatRoute.chat != null) {
            if (chatRoute.chatId == 0L) {
                launchBack {
                    project.chat.getChatOnUsers(chatRoute.chat.members)?.also { chat ->
                        project.profile.getAllProfilesOnUserIds(chat.members.toList()).also { users ->
                            chat.injectChatForChatScreen(userId, users).also { chatInjected ->
                                _uiState.update { state ->
                                    state.copy(chat = chatInjected, isProcess = false)
                                }
                                fetchRealTimeMessages(chatId = chatInjected.id, userId = userId)
                            }
                        }
                    } ?: kotlin.run {
                        project.profile.getAllProfilesOnUserIds(chatRoute.chat.members.toList()).also { users ->
                            chatRoute.chat.injectChatForChatScreen(userId, users).also { chatInjected ->
                                _uiState.update { state ->
                                    state.copy(chat = chatInjected, isProcess = false)
                                }
                                fetchRealTimeMessages(chatId = chatInjected.id, userId = userId)
                            }
                        }
                    }
                }
            } else {
                _uiState.update { state ->
                    state.copy(chat = chatRoute.chat, isProcess = false)
                }
                fetchRealTimeMessages(chatId = chatRoute.chat.id, userId = userId)
            }
        } else {
            launchBack {
                project.chat.getChatOnId(chatRoute.chatId)?.also { chat ->
                    project.profile.getAllProfilesOnUserIds(chat.members.toList()).also { users ->
                        chat.injectChatForChatScreen(userId, users).also { chatInjected ->
                            _uiState.update { state ->
                                state.copy(chat = chatInjected, isProcess = false)
                            }
                            fetchRealTimeMessages(chatId = chatInjected.id, userId = userId)
                        }
                    }
                } ?: setProcess(false)
            }
        }
    }

    private fun fetchRealTimeMessages(chatId: Long, userId: String) {
        launchBack {
            project.message.fetchRealTimeMessages(chatId) { messages ->
                uiState.value.apply {
                    if (this@apply.messages.isEmpty()) {
                        messages.markRead(userId)
                    }
                    messages.injectChatMessagesNormal(
                        this@apply.chat,
                        userId = userId
                    ).also { chatMessages ->
                        _uiState.update { state ->
                            state.copy(messages = chatMessages, dummy = state.dummy + 1, isProcess = false)
                        }
                    }
                }
            }
        }
    }

    fun onSend(senderId: String) {
        uiState.value.also { uiState ->
            launchBack {
                if (uiState.chat.id == 0L) {
                    project.chat.addNewChat(uiState.chat)?.also { chat ->
                        Message(
                            chatId = chat.id,
                            senderId = senderId,
                            content = uiState.chatText,
                            date = dateNow,
                            readersIds = arrayOf(senderId)
                        ).also { message ->
                            project.message.addNewMessage(message)?.also { firstMessage ->
                                chat.injectChatForChatScreen(senderId, uiState.chat.users.toList()).also { chatInjected ->
                                    listOf(firstMessage).injectChatMessages(
                                        uiState.chat,
                                        userId = senderId
                                    ).also { chatMessages ->
                                        _uiState.update { state ->
                                            state.copy(chat = chatInjected, messages = chatMessages, chatText = "", dummy = state.dummy + 1, isProcess = false)
                                        }
                                        fetchRealTimeMessages(chatId = chatInjected.id, userId = senderId)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Message(
                        chatId = uiState.chat.id,
                        senderId = senderId,
                        content = uiState.chatText,
                        date = dateNow,
                        readersIds = arrayOf(senderId)
                    ).also { message ->
                        project.message.addNewMessage(message)?.also {
                            uiState.messages.toMutableList().apply { add(it) }.injectChatMessages(
                                uiState.chat,
                                userId = senderId
                            ).also { chatMessages ->
                                _uiState.update { state ->
                                    state.copy(messages = chatMessages, chatText = "", dummy = state.dummy + 1 ,isProcess = false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun List<Message>.markRead(userId: String) {
        launchBack {
            filter {
                !it.readersIds.contains(userId)
            }.also { messages ->
                if (messages.isNotEmpty()) {
                    project.message.editMessages(
                        messages.map { it.copy(readersIds = it.readersIds.toMutableList().apply { add(userId) }.toTypedArray()) }
                    )
                }
            }
        }
    }

    fun onTextChanged(text: String) {
        _uiState.update { state ->
            state.copy(chatText = text)
        }
    }

    @Suppress("SameParameterValue")
    private fun setProcess(isProcess: Boolean = true) {
        _uiState.update { state ->
            state.copy(isProcess = isProcess)
        }
    }

    data class State(
        val chat: Chat = Chat(),
        val messages: List<Message> = emptyList(),
        val chatText: String = "",
        val dummy: Int = 0,
        val isProcess: Boolean = true,
    )

}