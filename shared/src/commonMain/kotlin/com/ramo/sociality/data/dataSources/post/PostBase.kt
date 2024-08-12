package com.ramo.sociality.data.dataSources.post

import com.ramo.sociality.data.model.Meme
import com.ramo.sociality.data.model.MemeData
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.model.Post
import com.ramo.sociality.data.model.User
import com.ramo.sociality.data.util.injectUsersForMemes
import com.ramo.sociality.global.util.logger

class PostBase(
    private val repo: PostRepo
) {

    suspend fun getPostOnId(id: Long): Post? = repo.getPostOnId(id)
    suspend fun getPostOnIdForeign(id: Long, invoke: (MemeData?) -> Unit) = repo.getPostOnIdForeign(id, invoke)
    suspend fun getPostsOnUser(id: String): List<Post> = repo.getPostsOnUser(id)
    suspend fun getPostsOnUserFriends(ids: List<Long>): List<Post> = repo.getPostsOnUserFriends(ids)
    suspend fun getAllPostsOnUser(users: List<User>, userId: String, profileId: String, invoke: (List<MemeLord>) -> Unit) {
        return repo.getAllPostsOnUser(profileId) { memes ->
            memes.injectUsersForMemes(users, userId).also(invoke)
        }
    }
    suspend fun getAllPostsOnUserFriends(users: List<User>, userId: String, invoke: (List<MemeLord>) -> Unit) {
        return repo.getAllPostsOnUserFriends(users.map { it.userId }) { memes ->
            logger(error = memes.size.toString())
            memes.injectUsersForMemes(users, userId).also(invoke)
        }
    }

    suspend fun addNewPost(item: Post): Post? = repo.addNewPost(item)
    suspend fun editPost(item: Post): Post? = repo.editPost(item)
    suspend fun deletePost(id: Long): Int = repo.deletePost(id)
}