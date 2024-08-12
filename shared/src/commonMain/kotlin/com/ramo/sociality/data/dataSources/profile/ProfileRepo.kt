package com.ramo.sociality.data.dataSources.profile

import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.model.User

interface ProfileRepo {

    suspend fun getProfileOnUserId(userId: String): User?
    suspend fun getProfileInfosOnUserId(userId: String, invoke: (User?, Friendship?, List<FriendshipRequest>) -> Unit)
    suspend fun getProfileOnEmail(email: String): User?
    suspend fun getProfileOnUsername(username: String): User?
    suspend fun getAllProfilesOnUserIds(ids: List<String>): List<User>
    suspend fun fetchProfilesOnName(name: String): List<User>
    suspend fun addNewUser(item: User): User?
    suspend fun editUser(item: User): User?
    suspend fun deleteUser(id: Long): Int
}