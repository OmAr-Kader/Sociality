package com.ramo.sociality.data.dataSources.message

import com.ramo.sociality.data.model.Message
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.global.base.SUPA_MESSAGE
import com.ramo.sociality.global.base.Supabase
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator

class MessageRepoImp(supabase: Supabase) : BaseRepoImp(supabase), MessageRepo {

    override suspend fun fetchRealTimeMessages(chatId: Long, invoke: (List<Message>) -> Unit) = queryRealTime(
        table = SUPA_MESSAGE,
        primaryKey = Message::id,
        filter = FilterOperation("chat_id", FilterOperator.EQ, chatId),
        invoke = invoke
    )

    override suspend fun fetchRealTimeMessagesOfChats(chatIds: List<Long>, invoke: (List<Message>) -> Unit) {
        queryRealTime(
            table = SUPA_MESSAGE,
            primaryKey = Message::id,
            filter = FilterOperation("chat_id", FilterOperator.IN, "(${chatIds.joinToString(",")})"),
            invoke = invoke
        )
    }

    override suspend fun addNewMessage(item: Message): Message? = insert(SUPA_MESSAGE, item)

    override suspend fun editMessages(items: List<Message>): List<Message>? = upsert(SUPA_MESSAGE, "id", items)

    override suspend fun editMessage(item: Message): Message? = edit(SUPA_MESSAGE, item.id, item)

    override suspend fun deleteMessage(id: Long): Int = delete(SUPA_MESSAGE, id)
}