package com.ramo.sociality.data.model

import com.ramo.sociality.data.util.BaseObject
import com.ramo.sociality.global.base.SUPA_FRIEND
import com.ramo.sociality.global.base.SUPA_FRIEND_REQUEST
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject

@Serializable
data class User(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("user_id")
    val userId: String = "",
    @SerialName("username")
    val username: String = "",
    @SerialName("email")
    val email: String = "",
    @SerialName("name")
    val name: String = "",
    @SerialName("bio")
    val bio: String = "",
    @SerialName("profile_picture")
    val profilePicture: String = "",
    @Transient
    val mode: Int = 0, // Addable = 0, Cancelable = -1, Acceptable = -2, Not Addable = 1, Own = 2
): BaseObject() {

    override fun json(): JsonObject {
        return kotlinx.serialization.json.Json.encodeToJsonElement(this.copy()).jsonObject.toMutableMap().apply {
            remove("id")
        }.let(::JsonObject)
    }
}


@Serializable
data class Profile(
    @SerialName(SUPA_FRIEND) val friendship: List<Friendship> = emptyList(),
    @SerialName(SUPA_FRIEND_REQUEST) val requests: List<FriendshipRequest> = emptyList(),
)

@Serializable
data class UserBase(
    @SerialName("id")
    val id: String = "",
    @SerialName("username")
    val username: String = "",
    @SerialName("email")
    val email: String = "",
    @Transient
    val name: String = "",
    @Transient
    val profilePicture: String = ""
)