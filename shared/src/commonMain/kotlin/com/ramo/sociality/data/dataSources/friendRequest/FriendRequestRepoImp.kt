package com.ramo.sociality.data.dataSources.friendRequest

import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.global.base.SUPA_FRIEND_REQUEST
import com.ramo.sociality.global.base.Supabase

class FriendRequestRepoImp(supabase: Supabase) : BaseRepoImp(supabase), FriendRequestRepo {

    override suspend fun getFriendshipRequestOnId(id: Long): FriendshipRequest? = querySingle(SUPA_FRIEND_REQUEST) {
        FriendshipRequest::id eq id
    }

    override suspend fun getFriendRequestsOnUser(userId: String): List<FriendshipRequest> = query(SUPA_FRIEND_REQUEST) {
        or {
            FriendshipRequest::userId eq userId
            FriendshipRequest::anotherUserId eq userId
        }
    }

    override suspend fun getFriendRequestsOnUserIds(ids: List<String>): List<FriendshipRequest> = query(SUPA_FRIEND_REQUEST) {
        or {
            FriendshipRequest::userId isIn ids
            FriendshipRequest::anotherUserId isIn ids
        }
    }

    override suspend fun addFriendRequest(item: FriendshipRequest): FriendshipRequest? = insert(SUPA_FRIEND_REQUEST, item)

    override suspend fun addFriendRequest(items: List<FriendshipRequest>): List<FriendshipRequest>? = insert(SUPA_FRIEND_REQUEST, items)

    override suspend fun deleteFriendRequest(id: Long): Int  = delete(SUPA_FRIEND_REQUEST, id)

    override suspend fun deleteFriendRequest(first: String, second: String): Int = deleteFilter(SUPA_FRIEND_REQUEST) {
        or {
            and {
                FriendshipRequest::userId eq first
                FriendshipRequest::anotherUserId eq second
            }
            and {
                FriendshipRequest::userId eq second
                FriendshipRequest::anotherUserId eq first
            }
        }
    }
}