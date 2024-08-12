package com.ramo.sociality.data.dataSources.like

import com.ramo.sociality.data.model.Like

interface LikeRepo {

    suspend fun addLike(item: Like): Like?
    suspend fun deleteLike(userId: String, postId: Long): Int
}