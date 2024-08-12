package com.ramo.sociality.data.model

import com.ramo.sociality.data.util.BaseObject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class Friendship(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",// FOREIGN
    @SerialName("friends")
    val friends: Array<String> = arrayOf(),
): BaseObject() {

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Friendship

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (!friends.contentEquals(other.friends)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + friends.contentHashCode()
        return result
    }
}


@Serializable
data class FriendshipRequest(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",// FOREIGN
    @SerialName("another_user_id")
    val anotherUserId: String,// FOREIGN
    @SerialName("date")
    val date: String = "",
    @SerialName("is_source")
    val isSource: Boolean
): BaseObject() {

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }

}