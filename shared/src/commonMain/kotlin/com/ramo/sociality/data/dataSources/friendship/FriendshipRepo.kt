package com.ramo.sociality.data.dataSources.friendship

import com.ramo.sociality.data.model.Friendship

interface FriendshipRepo {

    suspend fun getFriendshipOnId(userId: String): Friendship?
    suspend fun addNewFriendship(item: Friendship): Friendship?
    suspend fun editFriendship(item: Friendship): Friendship?
    suspend fun deleteFriendship(id: Long): Int
}