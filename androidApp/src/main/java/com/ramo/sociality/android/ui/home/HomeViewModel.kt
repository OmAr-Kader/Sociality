package com.ramo.sociality.android.ui.home

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.Comment
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.data.supaBase.signOutAuth
import com.ramo.sociality.data.util.REALM_SUCCESS
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.util.dateNow
import com.ramo.sociality.global.util.logger
import com.ramo.sociality.global.util.replace
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(project: Project) : BaseViewModel(project) {
    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun loadMemes(userId: String) {
        launchBack {
            project.friendship.getFriendshipOnId(userId)?.also { friends ->
                project.profile.getAllProfilesOnUserIds(friends.friends.toMutableList().apply { add(userId) }.toList()).let { profiles ->
                    project.post.getAllPostsOnUserFriends(profiles, userId) { memeLords ->
                        logger(error = memeLords.size.toString())
                        _uiState.update { state ->
                            state.copy(memes = memeLords, isProcess = false)
                        }
                    }
                }
            } ?: kotlin.run {
                setProcess(false)
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

    fun onValueComment(commentText: String) {
        _uiState.update { state ->
            state.copy(commentText = commentText)
        }
    }

    fun onShareClicked() {

    }

    fun hide() {
        _uiState.update { state ->
            state.copy(commentMeme = null, commentText = "")
        }
    }

    fun signOut(invoke: () -> Unit, failed: () -> Unit) {
        setProcess(true)
        launchBack {
            project.pref.deletePrefAll().checkDeleting({
                signOutAuth({
                    setProcess(false)
                    invoke()
                }, {
                    setProcess(false)
                    failed()
                })
            }, {
                setProcess(false)
                failed()
            })
        }
    }

    private suspend fun Int.checkDeleting(invoke: suspend () -> Unit, failed: suspend () -> Unit) {
        if (this@checkDeleting == REALM_SUCCESS) {
            invoke.invoke()
        } else {
            failed.invoke()
        }
    }

    private fun setProcess(isProcess: Boolean) {
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
        val memes: List<MemeLord> = emptyList(),
        val commentMeme: MemeLord? = null,
        val commentText: String = "",
        val dummy: Int = 0,
        val isProcess: Boolean = true,
        val isLocked: Boolean = false,
    )
}