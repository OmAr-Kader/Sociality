package com.ramo.sociality.data.dataSources.friendship

import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.global.base.SUPA_FRIEND
import com.ramo.sociality.global.base.Supabase

class FriendshipRepoImp(supabase: Supabase) : BaseRepoImp(supabase), FriendshipRepo {

    override suspend fun getFriendshipOnId(userId: String): Friendship?  = querySingle(SUPA_FRIEND) {
        Friendship::userId eq userId
    }

    override suspend fun addNewFriendship(item: Friendship): Friendship? = insert(SUPA_FRIEND, item)

    override suspend fun editFriendship(item: Friendship): Friendship? = edit(SUPA_FRIEND, item.id, item)

    override suspend fun deleteFriendship(id: Long): Int = delete(SUPA_FRIEND, id)
}