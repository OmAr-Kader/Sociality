package com.ramo.sociality.android.ui.profile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.BackButton
import com.ramo.sociality.android.global.ui.LoadingScreen
import com.ramo.sociality.android.global.ui.OnLaunchScreenScope
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.android.ui.common.CommentBottomSheet
import com.ramo.sociality.android.ui.common.PostItem
import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.User
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.base.CHAT_SCREEN_ROUTE
import com.ramo.sociality.global.base.POST_SCREEN_ROUTE
import com.ramo.sociality.global.base.PROFILE_SCREEN_ROUTE
import com.ramo.sociality.global.util.loggerError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileScreen(userBase: UserBase, screen: () -> Screen.ProfileRoute?, navigateToScreen: suspend (Screen, String) -> Unit, backPress: suspend () -> Unit, viewModel: ProfileViewModel = koinViewModel(), theme: Theme = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    OnLaunchScreenScope {
        screen()?.userId?.let { viewModel.loadData(userBase, it) }
    }
    Scaffold { padding ->
        Column(Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    ProfileHeader(
                        user = state.user,
                        onAddFriendClicked = { viewModel.onAddFriendClicked(userBase.id, state.user.userId) },
                        onAcceptFriendClicked = { viewModel.onAcceptFriendClicked(userBase, state.user.userId) },
                        onCancelFriendClicked = { viewModel.onCancelFriendClicked(userBase, state.user.userId) },
                        onMessageClicked = {
                            scope.launch {
                                navigateToScreen(Screen.ChatRoute(0L, Chat(members = arrayOf(userBase.id, state.user.userId))), CHAT_SCREEN_ROUTE)
                            }
                        }
                    )
                }
                items(state.memes) { meme ->
                    PostItem(
                        meme = meme,
                        theme = theme,
                        navigateToUser = {
                            scope.launch {
                                navigateToScreen(Screen.ProfileRoute(meme.user.userId), PROFILE_SCREEN_ROUTE)
                            }
                        },
                        navigateToImage = {
                            scope.launch {
                                navigateToScreen(Screen.PostRoute(meme.post.postMedia.toList(), it), POST_SCREEN_ROUTE)
                            }
                        },
                        onLikeClicked = { viewModel.onLikeClicked(userBase.id, meme.post.id, meme.isLiked) },
                        onCommentClicked = { viewModel.onCommentClicked(meme) },
                        onShareClicked = { viewModel.onShareClicked() }
                    )
                }
            }
        }
        BackButton(color = theme.textForPrimaryColor) {
            scope.launch { backPress() }
        }
        CommentBottomSheet(scrollState, state.commentMeme, state.commentText, viewModel::onValueComment, viewModel::hide) {
            viewModel.onComment(userBase, it.post.id) {
                scope.launch {
                    kotlinx.coroutines.delay(200L)
                    scrollState.animateScrollToItem(0)
                }
            }
        }
        LoadingScreen(state.isProcess, theme)
    }
}

@Composable
fun LazyItemScope.ProfileHeader(
    user: User,
    onAddFriendClicked: () -> Unit,
    onAcceptFriendClicked:() -> Unit,
    onCancelFriendClicked:() -> Unit,
    onMessageClicked: () -> Unit,
    theme: Theme = koinInject()
) {
    if (user.id == 0L) {
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateItem(spring(stiffness = Spring.StiffnessMediumLow), null, null),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        coil.compose.SubcomposeAsyncImage(
            model = LocalContext.current.imageBuildr(user.profilePicture),
            success = { (painter, _) ->
                Image(
                    painter = painter,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            error = {
                Image(
                    imageVector = rememberProfile(theme.textColor),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            onError = {
                loggerError("AsyncImagePainter.Error", it.result.throwable.stackTraceToString())
            },
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
            contentDescription = "Image"
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.name,
            color = theme.textColor,
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            text = user.bio,
            color = theme.textColor,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            when(user.mode) {
                0 -> {
                    Button(
                        onClick = onAddFriendClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                    ) {
                        Text(text = "Add Friend", color = Color.White)
                    }
                }
                -2 -> {
                    Button(
                        onClick = onAcceptFriendClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                    ) {
                        Text(text = "Accept Friend", color = Color.White)
                    }
                }
                -1 -> {
                    Button(
                        onClick = onCancelFriendClicked,
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                    ) {
                        Text(text = "Cancel Request", color = Color.White)
                    }
                }
                2 -> {
                    Button(
                        onClick = {

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = theme.primary)
                    ) {
                        Text(text = "Edit Profile", color = Color.White)
                    }
                }
            }
            if (user.mode != 2) {
                Button(
                    onClick = onMessageClicked,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                ) {
                    Text(text = "Message", color = Color.Black)
                }
            }
        }
    }
}
