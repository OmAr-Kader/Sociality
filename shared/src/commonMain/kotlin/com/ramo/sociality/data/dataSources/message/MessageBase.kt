package com.ramo.sociality.data.dataSources.message

import com.ramo.sociality.data.model.Message

class MessageBase(
    private val repo: MessageRepo
) {
    suspend fun fetchRealTimeMessages(chatId: Long, invoke: (List<Message>) -> Unit) = repo.fetchRealTimeMessages(chatId, invoke)
    suspend fun fetchRealTimeMessagesOfChats(chatIds: List<Long>, invoke: (List<Message>) -> Unit) = repo.fetchRealTimeMessagesOfChats(chatIds, invoke)
    suspend fun addNewMessage(item: Message): Message? = repo.addNewMessage(item)
    suspend fun editMessage(items: Message): Message? = repo.editMessage(items)
    suspend fun editMessages(item: List<Message>): List<Message>? = repo.editMessages(item)
    suspend fun deleteMessage(id: Long): Int = repo.deleteMessage(id)
}