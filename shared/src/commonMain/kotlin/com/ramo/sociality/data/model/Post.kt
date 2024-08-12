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
data class Post(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",// FOREIGN
    @SerialName("content")
    val content: Array<PostContent> = emptyArray(),
    @SerialName("media")
    val postMedia: Array<PostMedia> = emptyArray(),
    @SerialName("date")
    val date: String = "",
    @SerialName("last_edit")
    val lastEdit: String = "",
): BaseObject() {

    val dat: Instant get() = Instant.parse(date)

    val timestamp: String get() {
        return dat.format(DateTimeComponents.Format {
            byUnicodePattern("MM/dd HH:mm")
        })
    }
    @Transient
    val isHaveMedia: Boolean = postMedia.isNotEmpty()
    //val now = Clock.System.now()

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Post

        if (id != other.id) return false
        if (userId != other.userId) return false
        if (!content.contentEquals(other.content)) return false
        if (!postMedia.contentEquals(other.postMedia)) return false
        if (date != other.date) return false
        if (dat != other.dat) return false
        if (isHaveMedia != other.isHaveMedia) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + postMedia.contentHashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + dat.hashCode()
        result = 31 * result + isHaveMedia.hashCode()
        return result
    }

}

@Serializable
data class PostContent(
    @SerialName("font")
    val font: Int,
    @SerialName("text")
    val text: String = ""
)

@Serializable
data class PostMedia(
    @SerialName("media_type")
    val mediaType: Int = 0,
    @SerialName("media_URL")
    val mediaURL: String = "",
)