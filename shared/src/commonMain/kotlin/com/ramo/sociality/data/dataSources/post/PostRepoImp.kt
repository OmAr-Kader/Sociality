package com.ramo.sociality.data.dataSources.post

import com.ramo.sociality.data.model.Comment
import com.ramo.sociality.data.model.Like
import com.ramo.sociality.data.model.Meme
import com.ramo.sociality.data.model.MemeData
import com.ramo.sociality.data.model.Post
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.data.util.toListOfObject
import com.ramo.sociality.global.base.SUPA_COMMENT
import com.ramo.sociality.global.base.SUPA_LIKE
import com.ramo.sociality.global.base.SUPA_POST
import com.ramo.sociality.global.base.Supabase
import io.github.jan.supabase.postgrest.query.Columns

class PostRepoImp(supabase: Supabase) : BaseRepoImp(supabase), PostRepo {

    override suspend fun getPostOnId(id: Long): Post? = querySingle(SUPA_POST) {
        Post::id eq id
    }

    override suspend fun getPostOnIdForeign(id: Long, invoke: suspend (MemeData?) -> Unit)  {
        queryWithForeign(SUPA_POST, Columns.raw("*, $SUPA_COMMENT(*), $SUPA_LIKE(*)")) {
            Post::id eq id
        }?.apply {
            toListOfObject<Meme>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                toListOfObject<Post>(kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                })?.firstOrNull().let { if (it == Post()) null else it }?.let { post ->
                    result?.comments?.firstOrNull()?.let { if (it == Comment()) null else result.comments }?.let { comments ->
                        result.likes.firstOrNull()?.let { if (it == Like()) null else result.likes }?.let { likes ->
                            invoke(MemeData(post, comments, likes))
                        }
                    } ?: invoke(null)
                }
            }

        } ?: invoke(null)
    }


    override suspend fun getPostsOnUser(id: String): List<Post> = query<Post>(SUPA_POST) {
        Post::userId eq id
    }

    override suspend fun getPostsOnUserFriends(ids: List<Long>): List<Post> = query<Post>(SUPA_POST) {
        Post::userId isIn ids
    }

    override suspend fun getAllPostsOnUser(id: String, invoke: suspend (List<MemeData>) -> Unit) {
        queryWithForeign(SUPA_POST, Columns.raw("*, $SUPA_COMMENT(*), $SUPA_LIKE(*)")) {
            Post::userId eq id
        }?.apply {
            val posts = toListOfObject<Post>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }) ?: emptyList()
            val memes = toListOfObject<Meme>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }) ?: emptyList()
            mutableListOf<MemeData>().also { memesData ->
                val allLikes = memes.map { it.likes }.flatten()
                val allComment = memes.map { it.comments }.flatten()
                posts.onEach { post ->
                    val likes = allLikes.filter { it.postId == post.id }
                    val comments = allComment.filter { it.postId == post.id }
                    memesData.add(MemeData(post, comments, likes))
                }
                invoke(memesData)
            }
        } ?: invoke(emptyList())
    }

    override suspend fun getAllPostsOnUserFriends(ids: List<String>, invoke: suspend (List<MemeData>) -> Unit) {
        queryWithForeign(SUPA_POST, Columns.raw("*, $SUPA_COMMENT(*), $SUPA_LIKE(*)")) {
            Post::userId isIn ids
        }?.apply {
            val posts = toListOfObject<Post>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }) ?: emptyList()
            val memes = toListOfObject<Meme>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            }) ?: emptyList()
            mutableListOf<MemeData>().also { memesData ->
                val allLikes = memes.map { it.likes }.flatten()
                val allComment = memes.map { it.comments }.flatten()
                posts.onEach { post ->
                    val likes = allLikes.filter { it.postId == post.id }
                    val comments = allComment.filter { it.postId == post.id }
                    memesData.add(MemeData(post, comments, likes))
                }
                invoke(memesData)
            }
        } ?: invoke(emptyList())
    }


    override suspend fun addNewPost(item: Post): Post? = insert(SUPA_POST, item)

    override suspend fun editPost(item: Post): Post? = edit(SUPA_POST, item.id, item)

    override suspend fun deletePost(id: Long): Int = delete(SUPA_POST, id)
}