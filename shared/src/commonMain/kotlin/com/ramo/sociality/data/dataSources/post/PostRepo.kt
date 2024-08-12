package com.ramo.sociality.data.dataSources.post

import com.ramo.sociality.data.model.MemeData
import com.ramo.sociality.data.model.Post

interface PostRepo {
    suspend fun getPostOnId(id: Long): Post?
    suspend fun getPostOnIdForeign(id: Long, invoke: suspend (MemeData?) -> Unit)
    suspend fun getPostsOnUser(id: String): List<Post>
    suspend fun getPostsOnUserFriends(ids: List<Long>): List<Post>
    suspend fun getAllPostsOnUser(id: String, invoke: suspend (List<MemeData>) -> Unit)
    suspend fun getAllPostsOnUserFriends(ids: List<String>, invoke: suspend (List<MemeData>) -> Unit)
    suspend fun addNewPost(item: Post): Post?
    suspend fun editPost(item: Post): Post?
    suspend fun deletePost(id: Long): Int
}