package com.ramo.sociality.data.dataSources.chat

import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.ChatMessageData

class ChatBase(
    private val repo: ChatRepo
) {

    suspend fun getChatOnId(id: Long): Chat? = repo.getChatOnId(id)
    suspend fun getChatMessageOnId(id: Long): ChatMessageData? = repo.getChatMessageOnId(id)
    suspend fun getChatOnUsers(userIds: List<String>): Chat? = repo.getChatOnUsers(userIds)
    suspend fun getChatMessageOnUsers(userIds: List<String>): ChatMessageData? = repo.getChatMessageOnUsers(userIds)
    suspend fun getChatsOnUser(userIds: List<String>): List<Chat> = repo.getChatsOnUser(userIds)
    suspend fun getChatsMessagesOnUsers(userIds: List<String>): List<ChatMessageData> = repo.getChatsMessagesOnUsers(userIds)
    suspend fun addNewChat(item: Chat): Chat? = repo.addNewChat(item)
    suspend fun editChat(item: Chat): Chat? = repo.editChat(item)
    suspend fun deleteChat(id: Long): Int = repo.deleteChat(id)
}