package com.ramo.sociality.data.dataSources.like

import com.ramo.sociality.data.model.Like
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.global.base.SUPA_LIKE
import com.ramo.sociality.global.base.Supabase

class LikeRepoImp(supabase: Supabase) : BaseRepoImp(supabase), LikeRepo {

    override suspend fun addLike(item: Like): Like? = insert(SUPA_LIKE, item)

    override suspend fun deleteLike(userId: String, postId: Long): Int = deleteFilter(SUPA_LIKE) {
        and {
            Like::userId eq userId
            Like::postId eq postId
            Like::commentId eq 0L
        }
    }
}