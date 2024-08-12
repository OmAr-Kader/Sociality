package com.ramo.sociality.data.dataSources.profile

import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.model.Profile
import com.ramo.sociality.data.model.User
import com.ramo.sociality.data.util.BaseRepoImp
import com.ramo.sociality.data.util.toListOfObject
import com.ramo.sociality.global.base.SUPA_FRIEND
import com.ramo.sociality.global.base.SUPA_FRIEND_REQUEST
import com.ramo.sociality.global.base.SUPA_USER
import com.ramo.sociality.global.base.Supabase
import io.github.jan.supabase.postgrest.query.Columns

class ProfileRepoImp(supabase: Supabase) : BaseRepoImp(supabase), ProfileRepo {

    override suspend fun getProfileOnUserId(userId: String): User? = querySingle(SUPA_USER) {
        User::userId eq userId
    }

    override suspend fun getProfileInfosOnUserId(userId: String, invoke: (User?, Friendship?, List<FriendshipRequest>) -> Unit) {
        queryWithForeign(SUPA_USER, Columns.raw("*, $SUPA_FRIEND(*), $SUPA_FRIEND_REQUEST(*)")) {
            User::userId eq userId
        }?.apply {
            toListOfObject<Profile>(kotlinx.serialization.json.Json {
                ignoreUnknownKeys = true
            })?.firstOrNull().let { result ->
                invoke(
                    toListOfObject<User>(kotlinx.serialization.json.Json {
                        ignoreUnknownKeys = true
                    })?.firstOrNull().let { if (it == User()) null else it },
                    result?.friendship?.firstOrNull()?.let { if (it == Friendship()) null else it },
                    result?.requests?.firstOrNull()?.let { if (it.userId == "") emptyList() else result.requests } ?: emptyList(),
                )
            }

        } ?: invoke(null, null, emptyList())
    }

    override suspend fun getProfileOnEmail(email: String): User? = querySingle(SUPA_USER) {
        User::email eq email
    }

    override suspend fun getProfileOnUsername(username: String): User? = querySingle(SUPA_USER) {
        User::username eq username
    }

    override suspend fun getAllProfilesOnUserIds(ids: List<String>): List<User> = query(SUPA_USER) {
        User::userId isIn ids
    }

    override suspend fun fetchProfilesOnName(name: String): List<User> = query(SUPA_USER) {
        or {
            User::name ilike "%${name}%"
            User::username ilike "%${name}%"
        }
    }

    override suspend fun addNewUser(item: User): User? = insert(SUPA_USER, item)

    override suspend fun editUser(item: User): User? = edit(SUPA_USER, item.id, item)

    override suspend fun deleteUser(id: Long): Int = delete(SUPA_USER, id)
}

