package com.ramo.sociality.data.dataSources.friendship

import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.UserBase

class FriendshipBase(
    private val repo: FriendshipRepo
) {

    suspend fun getFriendshipOnId(userId: String): Friendship? = repo.getFriendshipOnId(userId)
    suspend fun addNewFriendship(item: Friendship): Friendship? = repo.addNewFriendship(item)
    suspend fun upsertFriendship(item: Friendship): Friendship? = repo.editFriendship(item)
    suspend fun doEditFriendships(friendship: Friendship, userBase: UserBase, userId: String, invoke: (Boolean) -> Unit) {
        editFriendship(friendship.copy(friends = friendship.friends.toMutableList().apply { add(userId) }))?.let {  // Add His Id to My friendShip
            getFriendshipOnId(userId)?.let { his ->// Get His friendShip
                editFriendship(it.copy(friends = his.friends.toMutableList().apply { add(userBase.id) }))?.also { // Add My Id to His friendShip
                    invoke(true)
                }
            }
        } ?: invoke(false)
    }
    suspend fun editFriendship(item: Friendship): Friendship? = repo.editFriendship(item)
    suspend fun deleteFriendship(id: Long): Int = repo.deleteFriendship(id)
}