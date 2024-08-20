package com.ramo.sociality.android.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.base.outlinedTextFieldStyle
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.BackButtonLess
import com.ramo.sociality.android.global.ui.OnLaunchScreen
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.ui.rememberSearch
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.data.model.User
import com.ramo.sociality.global.base.PROFILE_SCREEN_ROUTE
import com.ramo.sociality.global.util.loggerError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SearchScreen(navigateToScreen: suspend (Screen, String) -> Unit, backPress: suspend () -> Unit, viewModel: SearchViewModel = koinViewModel(), theme: Theme = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    OnLaunchScreen {
        viewModel.loadSearchHistory()
    }
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BackButtonLess(color = theme.textColor) {
                    scope.launch { backPress() }
                }
                Spacer(Modifier.width(10.dp))
                Row(
                    Modifier
                        .padding(start = 7.dp, end = 7.dp)
                        .weight(1F)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = state.searchText,
                        onValueChange = viewModel::onSearchQueryChange,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("Search Users") },
                        singleLine = true,
                        colors = theme.outlinedTextFieldStyle(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    )
                }
                Image(
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .background(color = theme.backDark, shape = CircleShape)
                        .clickable { viewModel.doSearch(state.searchText) }
                        .padding(8.dp),
                    imageVector = rememberSearch(theme.textColor),
                    contentScale = ContentScale.Fit,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (state.isSearchHistory) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.searches) { searchHistory ->
                        SearchItem(name = searchHistory.searchText, theme) {
                            viewModel.onSearchQueryChange(searchHistory.searchText)
                            viewModel.doSearch(searchHistory.searchText)
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = !state.isSearchHistory
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.users) { user ->
                        UserItem(user = user, theme) {
                            scope.launch {
                                navigateToScreen(Screen.ProfileRoute(user.userId), PROFILE_SCREEN_ROUTE)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchItem(name: String, theme: Theme, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            imageVector = rememberSearch(theme.textColor),
            contentDescription = "Profile Image",
            modifier = Modifier
                .size(35.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = name, color = theme.textColor)
    }
    HorizontalDivider(Modifier.padding(start = 15.dp, end = 15.dp))
}

@Composable
fun UserItem(user: User, theme: Theme, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        coil.compose.SubcomposeAsyncImage(
            model = LocalContext.current.imageBuildr(user.profilePicture),
            success = { (painter, _) ->
                Image(
                    painter = painter,
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            },
            error = {
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    imageVector = rememberProfile(color = theme.textColor),
                    contentDescription = "Profile"
                )
            },
            onError = {
                loggerError("AsyncImagePainter.Error", it.result.throwable.stackTraceToString())
            },
            contentScale = ContentScale.Crop,
            filterQuality = FilterQuality.None,
            contentDescription = "Image"
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = user.name, color = theme.textColor)
    }
    HorizontalDivider(Modifier.padding(start = 15.dp, end = 15.dp))
}