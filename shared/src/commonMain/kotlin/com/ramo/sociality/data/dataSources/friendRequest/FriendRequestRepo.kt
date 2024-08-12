package com.ramo.sociality.data.dataSources.friendRequest

import com.ramo.sociality.data.model.FriendshipRequest

interface FriendRequestRepo {

    suspend fun getFriendshipRequestOnId(id: Long): FriendshipRequest?
    suspend fun getFriendRequestsOnUser(userId: String): List<FriendshipRequest>
    suspend fun getFriendRequestsOnUserIds(ids: List<String>): List<FriendshipRequest>
    suspend fun addFriendRequest(item: FriendshipRequest): FriendshipRequest?
    suspend fun addFriendRequest(items: List<FriendshipRequest>): List<FriendshipRequest>?
    suspend fun deleteFriendRequest(id: Long): Int
    suspend fun deleteFriendRequest(first: String, second: String): Int
}