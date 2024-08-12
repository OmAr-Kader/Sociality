package com.ramo.sociality.android.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.FabItem
import com.ramo.sociality.android.global.ui.LoadingScreen
import com.ramo.sociality.android.global.ui.MultiFloatingActionButton
import com.ramo.sociality.android.global.ui.OnLaunchScreenScope
import com.ramo.sociality.android.global.ui.rememberChat
import com.ramo.sociality.android.global.ui.rememberExitToApp
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.ui.rememberSearch
import com.ramo.sociality.android.global.ui.rememberSocial
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.android.ui.common.CommentBottomSheet
import com.ramo.sociality.android.ui.common.PostItem
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.base.AUTH_SCREEN_ROUTE
import com.ramo.sociality.global.base.MESSENGER_SCREEN_ROUTE
import com.ramo.sociality.global.base.POST_CREATOR_ROUTE
import com.ramo.sociality.global.base.POST_SCREEN_ROUTE
import com.ramo.sociality.global.base.PROFILE_SCREEN_ROUTE
import com.ramo.sociality.global.base.SEARCH_SCREEN_ROUTE
import com.ramo.sociality.global.util.loggerError
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun HomeScreen(
    userBase: UserBase,
    navigateTo: suspend (String) -> Unit,
    navigateToScreen: suspend (Screen, String) -> Unit,
    navigateHome: suspend (String) -> Unit,
    viewModel: HomeViewModel = koinInject(),
    theme: Theme = koinInject()
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(androidx.compose.material3.DrawerValue.Closed)
    val scaffoldState = remember { SnackbarHostState() }
    val chatIcon = rememberChat(theme.textForPrimaryColor)
    val scrollState = rememberLazyListState()
    OnLaunchScreenScope {
        viewModel.loadMemes(userBase.id)
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(250.dp)
                    .fillMaxHeight(),
                drawerTonalElevation = 6.dp,
                drawerContainerColor = theme.backDark
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.0.dp),
                    shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp),
                    color = theme.primary,
                ) {
                    Row(
                        Modifier.padding(start = 16.dp, end = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Sociality",
                            color = theme.textForPrimaryColor
                        )
                    }
                }
                NavigationDrawerItem(
                    label = { Text(text = "Sign out") },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = theme.backDark,
                        selectedContainerColor = theme.backDark,
                    ),
                    icon = {
                        Icon(
                            imageVector = rememberExitToApp(theme.textColor),
                            modifier = Modifier.size(25.dp),
                            contentDescription = "Sign out"
                        )
                    },
                    selected = false,
                    onClick = {
                        viewModel.signOut({
                            scope.launch { navigateHome(AUTH_SCREEN_ROUTE) }
                        }) {
                            scope.launch {
                                scaffoldState.showSnackbar(message = "Failed")
                            }
                        }
                    }
                )
            }
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(scaffoldState) {
                    Snackbar(it, containerColor = theme.backDarkSec, contentColor = theme.textColor)
                }
            },
            floatingActionButton = {
                MultiFloatingActionButton(
                    items = remember {
                        listOf(FabItem(chatIcon, "Chat", theme.primary), FabItem(Icons.Default.Add, "Post Creator", theme.secondary))
                    },
                ) {
                    scope.launch { navigateTo(if (it == 0) MESSENGER_SCREEN_ROUTE else POST_CREATOR_ROUTE) }
                }
            }
        ) { padding ->
            BarMainScreen(userBase, openDrawer = {
                scope.launch {
                    drawerState.open()
                }
            } , onSearch = {
                scope.launch {
                    navigateTo(SEARCH_SCREEN_ROUTE)
                }
            }) {
                scope.launch {
                    navigateToScreen(Screen.ProfileRoute(userBase.id), PROFILE_SCREEN_ROUTE)
                }
            }
            Column(modifier = Modifier.padding(padding)) {
                Spacer(Modifier.height(60.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
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
}

@Composable
fun BarMainScreen(
    userBase: UserBase,
    theme: Theme = koinInject(),
    openDrawer: () -> Unit,
    onSearch: () -> Unit,
    onProfile: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .shadow(elevation = 15.dp, spotColor = theme.primary, ambientColor = theme.primary),
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = theme.background),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 15.dp, end = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = openDrawer,
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = theme.textColor
                    )
                }
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp),
                    imageVector = rememberSocial(),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                )
            }
            Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .background(color = theme.backDark, shape = CircleShape)
                        .clickable(onClick = onSearch)
                        .padding(8.dp),
                    imageVector = rememberSearch(theme.textColor),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(10.dp))

                coil.compose.SubcomposeAsyncImage(
                    model = LocalContext.current.imageBuildr(userBase.profilePicture),
                    success = { (painter, _) ->
                        Image(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(color = theme.backDark, shape = CircleShape)
                                .clip(CircleShape)
                                .clickable {
                                    onProfile()
                                },
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                    },
                    error = {
                        Image(
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                                .background(color = theme.backDark, shape = CircleShape)
                                .padding(8.dp)
                                .clickable {
                                    onProfile()
                                },
                            imageVector = rememberProfile(theme.textColor),
                            contentScale = ContentScale.Fit,
                            contentDescription = null,
                        )
                    },
                    onError = {
                        loggerError("AsyncImagePainter.Error", it.result.throwable.stackTraceToString())
                    },
                    contentScale = ContentScale.Crop,
                    filterQuality = FilterQuality.None,
                    contentDescription = "Image"
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
        }
    }
}
