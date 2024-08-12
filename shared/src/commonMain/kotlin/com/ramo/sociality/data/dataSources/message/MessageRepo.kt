package com.ramo.sociality.data.dataSources.message

import com.ramo.sociality.data.model.Message

interface MessageRepo {
    suspend fun fetchRealTimeMessages(chatId: Long, invoke: (List<Message>) -> Unit)
    suspend fun fetchRealTimeMessagesOfChats(chatIds: List<Long>, invoke: (List<Message>) -> Unit)
    suspend fun addNewMessage(item: Message): Message?
    suspend fun editMessage(item: Message): Message?
    suspend fun editMessages(items: List<Message>): List<Message>?
    suspend fun deleteMessage(id: Long): Int
}