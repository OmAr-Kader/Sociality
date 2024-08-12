package com.ramo.sociality.data.dataSources.comment

import com.ramo.sociality.data.model.Comment
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.global.base.SUPA_COMMENT
import com.ramo.sociality.global.base.Supabase

class CommentRepoImp(supabase: Supabase) : BaseRepoImp(supabase), CommentRepo {

    override suspend fun addComment(item: Comment): Comment? = insert(SUPA_COMMENT, item)

    override suspend fun editComment(item: Comment): Comment? = edit(SUPA_COMMENT, item.id, item)

    override suspend fun deleteComment(id: Long): Int = delete(SUPA_COMMENT, id)

}