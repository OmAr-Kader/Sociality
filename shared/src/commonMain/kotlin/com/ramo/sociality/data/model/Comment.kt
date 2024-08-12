package com.ramo.sociality.data.model

import com.ramo.sociality.data.util.BaseObject
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
data class Comment(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",// FOREIGN
    @SerialName("post_id")
    val postId: Long = 0,// FOREIGN
    @SerialName("content")
    val content: String = "",
    @SerialName("date")
    val date: String = "",
    @Transient
    val userName: String = "",
    @Transient
    val userImage: String = "",
): BaseObject() {

    val dat: Instant get() = Instant.parse(date)

    val timestamp: String get() {
        return dat.format(DateTimeComponents.Format {
            byUnicodePattern("MM/dd HH:mm")
        })
    }

    val datMilliseconds: Long get() {
        return dat.toEpochMilliseconds()
    }

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}