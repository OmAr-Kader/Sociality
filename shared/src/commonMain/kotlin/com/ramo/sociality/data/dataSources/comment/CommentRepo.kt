package com.ramo.sociality.data.dataSources.comment

import com.ramo.sociality.data.model.Comment

interface CommentRepo {

    suspend fun addComment(item: Comment): Comment?
    suspend fun editComment(item: Comment): Comment?
    suspend fun deleteComment(id: Long): Int
}