package com.ramo.sociality.data.dataSources.chat

import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.ChatMessage
import com.ramo.sociality.data.model.ChatMessageData
import com.ramo.sociality.data.model.Message
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.data.util.toListOfObject
import com.ramo.sociality.global.base.SUPA_CHAT
import com.ramo.sociality.global.base.SUPA_MESSAGE
import com.ramo.sociality.global.base.Supabase
import io.github.jan.supabase.postgrest.query.Columns

class ChatRepoImp(supabase: Supabase) : BaseRepoImp(supabase), ChatRepo {

    override suspend fun getChatOnId(id: Long): Chat? = querySingle(SUPA_CHAT) {
        Chat::id eq id
    }

    override suspend fun getChatMessageOnId(id: Long): ChatMessageData? {
        return queryWithForeign(SUPA_CHAT, Columns.raw("*, $SUPA_MESSAGE(*)")) {
            Chat::id eq id
        }?.let { res ->
            res.toListOfObject<ChatMessage>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                res.toListOfObject<Chat>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.firstOrNull().let { if (it == Chat()) null else it }?.let { chat ->
                    result?.messages?.firstOrNull()?.let { if (it == Message()) null else result.messages }?.let { messages ->
                        ChatMessageData(chat = chat, messages = messages)
                    }
                }
            }
        }
    }

    override suspend fun getChatOnUsers(userIds: List<String>): Chat? = querySingle(SUPA_CHAT) {
        Chat::members contained userIds
    }

    override suspend fun getChatMessageOnUsers(userIds: List<String>): ChatMessageData? {
        return queryWithForeign(SUPA_CHAT, Columns.raw("*, $SUPA_MESSAGE(*)")) {
            Chat::members contained userIds // contained: Only Have the items vs contains: Included that items and another
        }?.let { res ->
            res.toListOfObject<ChatMessage>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                res.toListOfObject<Chat>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }).let { chatNative ->
                    chatNative?.firstOrNull().let { if (it == Chat()) null else it }?.let { chat ->
                        result?.messages?.firstOrNull()?.let { if (it == Message()) null else result.messages }?.let { messages ->
                            ChatMessageData(chat = chat, messages = messages)
                        }
                    }
                }
            }
        }
    }

    override suspend fun getChatsOnUser(userIds: List<String>): List<Chat> = query(SUPA_CHAT) {
        Chat::members contains userIds
    }

    override suspend fun getChatsMessagesOnUsers(userIds: List<String>): List<ChatMessageData> {
        return queryWithForeign(SUPA_CHAT, Columns.raw("*, $SUPA_MESSAGE(*)")) {
            Chat::members contains userIds
        }?.let { res ->
            res.toListOfObject<ChatMessage>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.let { r -> r.firstOrNull()?.messages?.firstOrNull()?.let { if (it == Message()) null else r.map { m -> m.messages }.flatten() } }?.let { allMessages ->
                res.toListOfObject<Chat>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.let { it.firstOrNull().let { chat -> if (chat == Chat()) null else it } }?.let { chats ->
                    mutableListOf<ChatMessageData>().also { memesData ->
                        chats.onEach { chat ->
                            val messages = allMessages.filter { it.chatId == chat.id }
                            memesData.add(ChatMessageData(chat, messages))
                        }
                    }
                }
            }
        } ?: emptyList()
    }

    override suspend fun addNewChat(item: Chat): Chat? = insert(SUPA_CHAT, item)

    override suspend fun editChat(item: Chat): Chat? = edit(SUPA_CHAT, item.id, item)

    override suspend fun deleteChat(id: Long): Int = delete(SUPA_CHAT, id)
}