package com.ramo.sociality.data.dataSources.chat

import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.ChatMessageData

interface ChatRepo {
    suspend fun getChatOnId(id: Long): Chat?
    suspend fun getChatMessageOnId(id: Long): ChatMessageData?
    suspend fun getChatOnUsers(userIds: Array<String>): Chat?
    suspend fun getChatMessageOnUsers(userIds: Array<String>): ChatMessageData?
    suspend fun getChatsOnUser(userIds: List<String>): List<Chat>
    suspend fun getChatsMessagesOnUsers(userIds: List<String>): List<ChatMessageData>
    suspend fun addNewChat(item: Chat): Chat?
    suspend fun editChat(item: Chat): Chat?
    suspend fun deleteChat(id: Long): Int
}