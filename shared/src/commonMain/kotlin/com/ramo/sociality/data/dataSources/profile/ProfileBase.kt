package com.ramo.sociality.data.dataSources.profile

import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.model.User

class ProfileBase(
    private val repo: ProfileRepo
) {
    suspend fun getProfileOnUserId(userId: String): User? = repo.getProfileOnUserId(userId)
    suspend fun getProfileInfosOnUserId(userId: String, invoke: (User?, Friendship?, List<FriendshipRequest>) -> Unit) = repo.getProfileInfosOnUserId(userId, invoke)
    suspend fun getProfileOnEmail(email: String): User? = repo.getProfileOnEmail(email)
    suspend fun getProfileOnUsername(username: String): User? = repo.getProfileOnUsername(username)
    suspend fun getAllProfilesOnUserIds(ids: List<String>): List<User> = repo.getAllProfilesOnUserIds(ids)
    suspend fun fetchProfilesOnName(name: String): List<User> = repo.fetchProfilesOnName(name)
    suspend fun addNewUser(item: User): User? = repo.addNewUser(item)
    suspend fun editUser(item: User): User? = repo.editUser(item)
    suspend fun deleteUser(id: Long): Int = repo.deleteUser(id)
}