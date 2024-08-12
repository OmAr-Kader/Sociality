package com.ramo.sociality.data.dataSources.comment

import com.ramo.sociality.data.model.Comment
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.util.dateNow
import com.ramo.sociality.global.util.replace

class CommentBase(
    private val repo: CommentRepo
) {

    suspend fun commenter(memes: List<MemeLord>, userBase: UserBase, postId: Long, commentText: String, invoke: (List<MemeLord>, MemeLord?) -> Unit) {
        addComment(
            Comment(userId = userBase.id, postId = postId, content = commentText, date = dateNow)
        )?.also { comment ->
            memes.toMutableList().replace({ it.post.id == postId },{ meme ->
                meme.comments.toMutableList().apply {
                    this@apply.add(comment.copy(userName = userBase.name, userImage = userBase.profilePicture))
                }.toList().let { newComments ->
                    meme.copy(comments = newComments.sortedByDescending { it.datMilliseconds }.toMutableList())
                }
            }) { newMemes, item ->
                invoke(newMemes, item)
            }
        } ?: invoke(memes, null)
    }

    suspend fun addComment(item: Comment): Comment? = repo.addComment(item)
    suspend fun editComment(item: Comment): Comment? = repo.editComment(item)
    suspend fun deleteComment(id: Long): Int = repo.deleteComment(id)
}