package com.ramo.sociality.data.dataSources.like

import com.ramo.sociality.data.model.Like
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.util.REALM_SUCCESS
import com.ramo.sociality.global.util.dateNow
import com.ramo.sociality.global.util.replace

class LikeBase(
    private val repo: LikeRepo
) {

    suspend fun liker(memes: List<MemeLord>, userId: String, postId: Long, isLiked: Boolean, invoke: (List<MemeLord>, Boolean) -> Unit) {
        if (!isLiked) {
            addLike(Like(userId = userId, postId = postId, commentId = 0L, date = dateNow))?.also { like ->
                memes.toMutableList().replace({ it.post.id == postId }) { meme ->
                    meme.copy(likes = meme.likes.toMutableList().apply { add(like) }, isLiked = true)
                }.also { newMemes ->
                    invoke(newMemes, true)
                }
            } ?: invoke(memes, false)
        } else {
            deleteLike(userId = userId, postId = postId)?.also { _ ->
                memes.toMutableList().replace({ it.post.id == postId }) { meme ->
                    meme.copy(likes = meme.likes.toMutableList().apply {
                        this@apply.removeAll {
                            it.userId == userId && it.postId == postId && it.commentId == 0L
                        }
                    }, isLiked = false)
                }.also { newMemes ->
                    invoke(newMemes, true)
                }
            } ?: invoke(memes, false)
        }
    }

    suspend fun addLike(item: Like): Like? = repo.addLike(item)
    suspend fun deleteLike(userId: String, postId: Long): Int? = repo.deleteLike(userId = userId, postId = postId).let {
        if (it == REALM_SUCCESS) it else null
    }
}