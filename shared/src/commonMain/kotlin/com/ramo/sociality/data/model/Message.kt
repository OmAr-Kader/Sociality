package com.ramo.sociality.data.model

import com.ramo.sociality.data.util.BaseObject
import com.ramo.sociality.global.base.SUPA_MESSAGE
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long = 0L,
    @SerialName("chat_tittle")
    val chartTitle: String = "",
    @SerialName("members")
    val members: Array<String> = emptyArray(),// FOREIGN KEYS
    @SerialName("chat_image")
    val chatImage: String = "",
    @Transient
    val users: Array<User> = emptyArray(),
    @Transient
    val chatPic: String = "",
    @Transient
    val chatLabel: String = "",
    @Transient
    val lastMessage: String = "",
    @Transient
    val timestampLastMessage: String = "",
    @Transient
    val numberOfLastMessages: Int = 0,
): BaseObject() {

    @Transient
    val showSenderName: Boolean = members.size > 2

    @Transient
    val lastMessageLineLess: String = lastMessage.lines().joinToString(". ")

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Chat

        if (id != other.id) return false
        if (chartTitle != other.chartTitle) return false
        if (!members.contentEquals(other.members)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + chartTitle.hashCode()
        result = 31 * result + members.contentHashCode()
        return result
    }

}

@Serializable
data class Message(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("chat_id")
    val chatId: Long = 0,// FOREIGN
    @SerialName("sender_id")
    val senderId: String = "",// FOREIGN
    @SerialName("content")
    val content: String = "",
    @SerialName("date")
    val date: String = "",
    @SerialName("readers_ids")
    val readersIds: Array<String> = emptyArray(),
    @Transient
    val isSeen: Boolean = false,
    @Transient
    val isSender: Boolean = false,
    @Transient
    val senderName: String = "",
): BaseObject() {

    val dat: Instant get() = Instant.parse(date)

    val timestamp: String get() {
        return dat.format(DateTimeComponents.Format {
            byUnicodePattern("MM/dd HH:mm")
        })
    }
    //val now = Clock.System.now()

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Message

        if (id != other.id) return false
        if (chatId != other.chatId) return false
        if (senderId != other.senderId) return false
        if (content != other.content) return false
        if (date != other.date) return false
        if (!readersIds.contentEquals(other.readersIds)) return false
        if (isSeen != other.isSeen) return false
        if (isSender != other.isSender) return false
        if (senderName != other.senderName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + chatId.hashCode()
        result = 31 * result + senderId.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + readersIds.contentHashCode()
        result = 31 * result + isSeen.hashCode()
        result = 31 * result + isSender.hashCode()
        result = 31 * result + senderName.hashCode()
        return result
    }

}

@Serializable
data class ChatMessage(
    @SerialName(SUPA_MESSAGE)
    val messages: List<Message> = emptyList()
)

data class ChatMessageData(
    val chat: Chat,
    val messages: List<Message>,
)