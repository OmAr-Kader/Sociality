package com.ramo.sociality.android.ui.post

import com.ramo.sociality.android.global.navigation.BaseViewModel
import com.ramo.sociality.data.model.Post
import com.ramo.sociality.data.model.PostContent
import com.ramo.sociality.data.model.PostMedia
import com.ramo.sociality.di.Project
import com.ramo.sociality.global.base.HEADLINE_FONT
import com.ramo.sociality.global.util.dateNow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PostCreatorViewModel(project: Project) : BaseViewModel(project) {

    private val _uiState = MutableStateFlow(State())
    val uiState = _uiState.asStateFlow()

    fun createPost(userId: String, invoke: (Post) -> Unit, failed: () -> Unit) {
        uiState.value.postCreate.also { post ->
            if (post.content.firstOrNull()?.text.isNullOrEmpty()) {
                _uiState.update { state ->
                    state.copy(isErrorPressed = false)
                }
                return
            }
            dateNow.also {
                doCreatePost(post.copy(userId = userId, date = it, lastEdit = it), invoke, failed)
            }
        }
    }

    private fun doCreatePost(post: Post, invoke: (Post) -> Unit, failed: () -> Unit) {
        launchBack {
            project.post.addNewPost(post)?.also(invoke) ?: failed()
        }
    }


    fun onMediaSelected(mediaType: Int, mediaURL: String) {
        _uiState.update { state ->
            state.copy(
                postCreate = state.postCreate.copy(
                    postMedia = state.postCreate.postMedia.toMutableList().apply {
                        add(PostMedia(mediaType = mediaType, mediaURL = mediaURL))
                    }
                )
            )
        }
    }

    fun makeFontDialogVisible() {
        _uiState.update { state ->
            state.copy(isFontDialogVisible = true)
        }
    }

    fun addAbout(font: Int) {
        val list = uiState.value.postCreate.content.toMutableList()
        list.add(PostContent(text = "", font = font))
        _uiState.update { state ->
            state.copy(postCreate = state.postCreate.copy(content = list), isFontDialogVisible = false, isErrorPressed = false)
        }
    }

    fun removeAboutIndex(index: Int) {
        val list = uiState.value.postCreate.content.toMutableList()
        list.removeAt(index)
        _uiState.update { state ->
            state.copy(postCreate = state.postCreate.copy(content = list), dummy = state.dummy + 1)
        }
    }

    fun changeAbout(it: String, index: Int) {
        val list = uiState.value.postCreate.content.toMutableList()
        list[index] = list[index].copy(text = it)
        _uiState.update { state ->
            state.copy(postCreate = state.postCreate.copy(content = list), dummy = state.dummy + 1)
        }
    }

    data class State(
        val postCreate: Post = Post().copy(content = listOf(PostContent(font = HEADLINE_FONT))),
        val dummy: Int = 0,
        val isFontDialogVisible: Boolean = false,
        val isErrorPressed: Boolean = false,
        val isProcess: Boolean = false,
    )

}