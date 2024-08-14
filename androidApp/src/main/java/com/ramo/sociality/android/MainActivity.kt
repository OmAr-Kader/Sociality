package com.ramo.sociality.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ramo.sociality.android.global.base.MyApplicationTheme
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.OnLaunchScreenScope
import com.ramo.sociality.android.global.ui.rememberSocial
import com.ramo.sociality.android.ui.chat.ChatScreen
import com.ramo.sociality.android.ui.chat.MessengerScreen
import com.ramo.sociality.android.ui.home.HomeScreen
import com.ramo.sociality.android.ui.post.PostCreatorScreen
import com.ramo.sociality.android.ui.post.PostScreen
import com.ramo.sociality.android.ui.profile.ProfileScreen
import com.ramo.sociality.android.ui.profile.SearchScreen
import com.ramo.sociality.android.ui.sign.AuthScreen
import com.ramo.sociality.global.base.AUTH_SCREEN_ROUTE
import com.ramo.sociality.global.base.CHAT_SCREEN_ROUTE
import com.ramo.sociality.global.base.HOME_SCREEN_ROUTE
import com.ramo.sociality.global.base.MESSENGER_SCREEN_ROUTE
import com.ramo.sociality.global.base.POST_CREATOR_ROUTE
import com.ramo.sociality.global.base.POST_SCREEN_ROUTE
import com.ramo.sociality.global.base.PROFILE_SCREEN_ROUTE
import com.ramo.sociality.global.base.SEARCH_SCREEN_ROUTE
import com.ramo.sociality.global.base.SPLASH_SCREEN_ROUTE
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Main()
        }
    }
}

@Composable
fun Main() {
    val theme: Theme = koinInject()
    val appViewModel: AppViewModel = koinViewModel()
    val stateApp by appViewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val navigateHome: suspend (String) -> Unit = { route ->
        navController.navigate(route = route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }
    val navigateTo: suspend (String) -> Unit = { route ->
        navController.navigate(route = route)
    }
    val navigateToScreen: suspend (Screen, String) -> Unit = { screen , route ->
        appViewModel.writeArguments(screen)
        kotlinx.coroutines.coroutineScope {
            navController.navigate(route = route)
        }
    }
    val backPress: suspend () -> Unit = {
        navController.navigateUp()
    }

    MyApplicationTheme(theme = theme) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = theme.background
        ) {
            Surface(
                color = theme.background
            ) {
                NavHost(
                    navController = navController,
                    startDestination = SPLASH_SCREEN_ROUTE
                ) {
                    composable(route = SPLASH_SCREEN_ROUTE) {
                        SplashScreen(navigateHome = navigateHome, appViewModel = appViewModel)
                    }
                    composable(route = AUTH_SCREEN_ROUTE) {
                        AuthScreen(appViewModel = appViewModel, navigateHome = navigateHome)
                    }
                    composable(route = HOME_SCREEN_ROUTE) {
                        HomeScreen(userBase = stateApp.userBase, navigateTo = navigateTo, navigateToScreen = navigateToScreen, navigateHome = navigateHome)
                    }
                    composable(route = SEARCH_SCREEN_ROUTE) {
                        SearchScreen(navigateToScreen = navigateToScreen, backPress = backPress)
                    }
                    composable(route = PROFILE_SCREEN_ROUTE) {
                        ProfileScreen(userBase = stateApp.userBase, screen = appViewModel::findArg, navigateToScreen = navigateToScreen, backPress = backPress)
                    }
                    composable(route = POST_CREATOR_ROUTE) {
                        PostCreatorScreen(stateApp.userBase, backPress = backPress)
                    }
                    composable(route = POST_SCREEN_ROUTE) {
                        PostScreen(screen = appViewModel::findArg, backPress = backPress)
                    }
                    composable(route = MESSENGER_SCREEN_ROUTE) {
                        MessengerScreen(userBase = stateApp.userBase, navigateToScreen = navigateToScreen, backPress = backPress)
                    }
                    composable(route = CHAT_SCREEN_ROUTE) {
                        ChatScreen(userBase = stateApp.userBase, screen = appViewModel::findArg, backPress = backPress)
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    navigateHome: suspend (String) -> Unit,
    appViewModel: AppViewModel,
    theme: Theme = koinInject(),
) {
    val scope = rememberCoroutineScope()
    OnLaunchScreenScope {
        appViewModel.findUserLive {
            scope.launch {
                navigateHome(if (it == null) AUTH_SCREEN_ROUTE else HOME_SCREEN_ROUTE)
            }
        }
    }
    Surface(color = theme.background) {
        androidx.compose.foundation.layout.Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
            AnimatedVisibility(
                visible = true,
                modifier = Modifier
                    .size(100.dp),
                enter = fadeIn(initialAlpha = 0.3F) + expandIn(expandFrom = androidx.compose.ui.Alignment.Center),
                label = "AppIcon"
            ) {
                Image(
                    imageVector = rememberSocial(),
                    contentScale = ContentScale.Fit,
                    contentDescription = "AppIcon",
                )
            }
        }
    }
}
