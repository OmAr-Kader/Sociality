package com.ramo.sociality.android.ui.profile

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.Friendship
import com.ramo.sociality.data.model.FriendshipRequest
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.model.User
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.data.util.REALM_SUCCESS
import com.ramo.sociality.data.util.getProfileMode
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.util.logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun loadData(userBase: UserBase, userId: String) {
        launchBack {
            project.profile.getProfileOnUserId(userId)?.also { user -> // Get User
                fetchMyProfileInfo(userBase.id, userId) { mode, friendShip, requests -> // getMyFriendShips, and Requests
                    _uiState.update { state ->
                        state.copy(user = user.copy(mode = mode), requests = requests, friendShip = friendShip, isProcess = false)
                    }
                    loadMemes(userBase, userId)
                }
            } ?: setProcess(false)
        }
    }

    private fun loadMemes(userBase: UserBase, userId: String) {
        launchBack {
            project.friendship.getFriendshipOnId(userId)?.also { friends ->
                project.profile.getAllProfilesOnUserIds(friends.friends.toMutableList().apply { add(userId) }.toList()).let { profiles ->
                    project.post.getAllPostsOnUser(profiles, userId = userBase.id, profileId = userId) { memeLords ->
                        _uiState.update { state ->
                            state.copy(memes = memeLords, isProcess = false)
                        }
                    }
                }
            } ?: setProcess(false)
        }
    }

    private suspend fun fetchMyProfileInfo(myId: String, userId: String, invoke: (Int, Friendship, List<FriendshipRequest>) -> Unit) {
        project.profile.getProfileInfosOnUserId(myId) { _, friendShip, requests -> // getMyFriendShips, and Requests
            if (friendShip == null) {
                setProcess(false)
                return@getProfileInfosOnUserId
            }
            requests.getProfileMode(myId, userId, friendShip).also {
                invoke(it, friendShip, requests)
            }
        }
    }

    fun onAddFriendClicked(fromMe: String, to: String) { // Addable = 0
        setProcess()
        uiState.value.also { uiState ->
            launchBack {
                project.friendRequest.doAddFriendRequest(requests = uiState.requests, fromMe = fromMe, to = to)?.let { requests ->
                    requests.getProfileMode(fromMe, to, uiState.friendShip).also { mode ->
                        _uiState.update { state ->
                            state.copy(user = state.user.copy(mode = mode), requests = requests, isProcess = false)
                        }
                    }
                } ?: setProcess(false)
            }
        }
    }

    fun onAcceptFriendClicked(userBase: UserBase, userId: String) { // Acceptable = -2
        setProcess()
        uiState.value.also { state ->
            launchBack {
                editFriendships(userBase, userId) {
                    project.friendRequest.acceptFriendRequest(requests = state.requests, myId = userBase.id, userId = userId).also {
                        if (it == REALM_SUCCESS) {
                            fetchMyProfileInfo(userBase.id, userId) { mode, friendShip, requests -> // getMyFriendShips, and Requests
                                _uiState.update { state ->
                                    state.copy(user = state.user.copy(mode = mode), friendShip = friendShip, requests = requests, isProcess = false)
                                }
                            }
                        } else {
                            setProcess(false)
                        }
                    }
                }
            }
        }
    }

    private suspend fun editFriendships(userBase: UserBase, userId: String, invoke: suspend () -> Unit) {
        uiState.value.also { uiState ->
            launchBack {
                project.friendship.doEditFriendships(uiState.friendShip, userBase = userBase, userId = userId) { isSuccess ->
                    if (isSuccess) {
                        invoke()
                    } else {
                        setProcess(false)
                    }
                }
            }
        }
    }

    fun onCancelFriendClicked(userBase: UserBase, userId: String) { // Cancelable = -1
        uiState.value.also { uiState ->
            if(uiState.isLocked) {
                logger("isLocked", "true")
                return
            }
            setLock(true)
            launchBack {
                project.friendRequest.doCancelFriendRequest(uiState.requests, userBase = userBase, userId = userId) { newRequests, isSuccess ->
                    if (isSuccess) {
                        _uiState.update { state ->
                            state.copy(user = state.user.copy(mode = 0), requests = newRequests, isLocked = false)
                        }
                    } else {
                        setLock(true)
                    }
                }
            }
        }
    }

    fun onLikeClicked(userId: String, postId: Long, isLiked: Boolean) {
        val uiState = uiState.value
        if(uiState.isLocked) {
            logger("isLocked", "true")
            return
        }
        setLock(true)
        launchBack {
            project.like.liker(memes = uiState.memes, userId = userId, postId = postId, isLiked = isLiked) { newMemes, isSuccess ->
                if (isSuccess) {
                    _uiState.update { state ->
                        state.copy(memes = newMemes, dummy = state.dummy + 1, isLocked = false)
                    }
                } else {
                    setLock(false)
                }
            }
        }
    }

    fun onComment(userBase: UserBase, postId: Long, invoke: () -> Unit) {
        val uiState = uiState.value
        if(uiState.isLocked || uiState.commentText.isEmpty()) {
            logger("isLocked", uiState.isLocked.toString())
            return
        }
        setLock(true)
        launchBack {
            project.comment.commenter(memes = uiState.memes, userBase = userBase, postId = postId, commentText = uiState.commentText) { newMemes, item ->
                if (item != null) {
                    _uiState.update { state ->
                        state.copy(memes = newMemes, commentMeme = item, commentText = "", dummy = state.dummy + 1, isLocked = false)
                    }
                    invoke()
                } else {
                    setLock(false)
                }
            }
        }
    }

    fun onCommentClicked(memeLord: MemeLord) {
        _uiState.update { state ->
            state.copy(commentMeme = memeLord, commentText = "")
        }
    }

    fun onShareClicked() {

    }

    fun hide() {
        _uiState.update { state ->
            state.copy(commentMeme = null, commentText = "")
        }
    }

    fun onValueComment(commentText: String) {
        _uiState.update { state ->
            state.copy(commentText = commentText)
        }
    }

    private fun setProcess(isProcess: Boolean = true) {
        _uiState.update { state ->
            state.copy(isProcess = isProcess)
        }
    }

    private fun setLock(isLocked: Boolean) {
        _uiState.update { state ->
            state.copy(isLocked = isLocked)
        }
    }

    data class State(
        val user: User = User(),
        val friendShip: Friendship = Friendship(),
        val requests: List<FriendshipRequest> = listOf(),
        val memes: List<MemeLord> = emptyList(),
        val commentMeme: MemeLord? = null,
        val commentText: String = "",
        val dummy: Int = 0,
        val isProcess: Boolean = true,
        val isLocked: Boolean = false
    )
}