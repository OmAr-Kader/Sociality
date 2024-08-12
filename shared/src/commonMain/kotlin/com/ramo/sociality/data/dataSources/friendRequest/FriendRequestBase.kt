package com.ramo.sociality.data.dataSources.friendRequest

import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.data.util.REALM_FAILED
import com.ramo.sociality.data.util.REALM_SUCCESS
import com.ramo.sociality.global.util.dateNow

@Suppress("MemberVisibilityCanBePrivate")
class FriendRequestBase(
    private val repo: FriendRequestRepo
) {

    suspend fun getFriendshipRequestOnId(id: Long): FriendshipRequest? = repo.getFriendshipRequestOnId(id)
    suspend fun getFriendRequestsOnUser(userId: String): List<FriendshipRequest> = repo.getFriendRequestsOnUser(userId)
    suspend fun getFriendRequestsOnUserIds(ids: List<String>): List<FriendshipRequest> = repo.getFriendRequestsOnUserIds(ids)
    suspend fun addFriendRequest(item: FriendshipRequest): FriendshipRequest? = repo.addFriendRequest(item)

    suspend fun doAddFriendRequest(requests: List<FriendshipRequest>, fromMe: String, to: String): List<FriendshipRequest>? {
        return addFriendRequest(
            dateNow.let {
                listOf(
                    FriendshipRequest(userId = fromMe, anotherUserId = to, date = it, isSource = true),
                    FriendshipRequest(userId = to, anotherUserId = fromMe, date = it, isSource = false),
                )
            }
        )?.let {
            requests.toMutableList().apply { addAll(it) }
        }
    }
    suspend fun addFriendRequest(items: List<FriendshipRequest>): List<FriendshipRequest>? = repo.addFriendRequest(items)
    suspend fun deleteFriendRequest(id: Long): Int = repo.deleteFriendRequest(id)

    suspend fun acceptFriendRequest(requests: List<FriendshipRequest>, myId: String, userId: String): Int {
        return requests.find { it.userId == myId && it.anotherUserId == userId && !it.isSource }?.let { friendshipRequest ->
            deleteFriendRequest(friendshipRequest.userId, friendshipRequest.anotherUserId)
        } ?: REALM_FAILED
    }

    suspend fun doCancelFriendRequest(requests: List<FriendshipRequest>, userBase: UserBase, userId: String, invoke: (List<FriendshipRequest>, Boolean) -> Unit) {
        requests.find { it.userId == userBase.id && it.anotherUserId == userId && it.isSource }?.also { friendshipRequest ->
            deleteFriendRequest(friendshipRequest.userId, friendshipRequest.anotherUserId).let {
                if (it == REALM_SUCCESS) {
                    getFriendRequestsOnUser(userBase.id).also { requests ->
                        invoke(requests, true)
                    }
                } else {
                    invoke(requests, false)
                }
            }
        } ?: kotlin.run {
            invoke(requests, false)
        }
    }

    suspend fun deleteFriendRequest(first: String, second: String): Int = repo.deleteFriendRequest(first, second)
}