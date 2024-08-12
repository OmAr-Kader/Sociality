package com.ramo.sociality.data.model

import com.ramo.sociality.global.base.SUPA_COMMENT
import com.ramo.sociality.global.base.SUPA_LIKE
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Meme(
    @SerialName(SUPA_LIKE) val likes: List<Like> = emptyList(),
    @SerialName(SUPA_COMMENT) val comments: List<Comment> = emptyList(),
)

data class MemeData(
    val post: Post,
    val comments: List<Comment>,
    val likes: List<Like>,
)

data class MemeLord(
    val user: User,
    val post: Post,
    val comments: List<Comment>,
    val likes: List<Like>,
    val isLiked: Boolean = false,
)
