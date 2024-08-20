package com.ramo.sociality.android.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.base.darker
import com.ramo.sociality.android.global.base.outlinedTextFieldStyle
import com.ramo.sociality.android.global.ui.BackButton
import com.ramo.sociality.android.global.ui.LoadingScreen
import com.ramo.sociality.data.model.Post
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.base.DETAILS_FONT
import com.ramo.sociality.global.base.HEADLINE_FONT
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PostCreatorScreen(userBase: UserBase, backPress: suspend () -> Unit, viewModel: PostCreatorViewModel = koinViewModel(), theme: Theme = koinInject()) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = remember { SnackbarHostState() }
    val scrollState = rememberLazyListState()
    Scaffold(
        snackbarHost = {
            SnackbarHost(scaffoldState) {
                Snackbar(it, containerColor = theme.backDarkSec, contentColor = theme.textColor)
            }
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background.darker())
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Spacer(modifier = Modifier.height(60.dp))
                BasicsViewPostCreator(
                    post = state.postCreate,
                    isFontDialogVisible = state.isFontDialogVisible,
                    scrollState = scrollState,
                    makeFontDialogVisible = viewModel::makeFontDialogVisible,
                    addAbout = viewModel::addAbout,
                    changeAbout = viewModel::changeAbout,
                    removeAboutIndex = viewModel::removeAboutIndex,
                ) {
                    scope.launch {
                        scrollState.animateScrollBy(70F)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = {
                        // Simulate media selection
                        viewModel.onMediaSelected(1, "https://example.com/media.jpg")
                    }) {
                        Text("Add Image")
                    }
                    Button(onClick = {
                        // Simulate media selection
                        viewModel.onMediaSelected(2, "https://example.com/video.mp4")
                    }) {
                        Text("Add Video")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.createPost(userBase.id, {
                            scope.launch { backPress() }
                        }) {
                            scope.launch { scaffoldState.showSnackbar("Failed") }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = theme.primary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Post", color = Color.White)
                }
            }
        }
        BackButton(color = theme.textForPrimaryColor) {
            scope.launch { backPress() }
        }
        LoadingScreen(state.isProcess, theme)
    }
}

@Composable
fun ColumnScope.BasicsViewPostCreator(
    post: Post,
    isFontDialogVisible: Boolean,
    scrollState: LazyListState,
    makeFontDialogVisible: () -> Unit,
    addAbout: (Int) -> Unit,
    changeAbout: (String, Int) -> Unit,
    removeAboutIndex: (Int) -> Unit,
    theme: Theme = koinInject(),
    scrollToEnd: (Int) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1F), contentPadding = PaddingValues(16.dp), state = scrollState) {
        itemsIndexed(post.content) { i, (font, text) ->
            val isHeadline = font > HEADLINE_FONT
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = text,
                onValueChange = {
                    changeAbout(it, i)
                },
                placeholder = { Text(text = if (isHeadline) "Enter About Headline" else "Enter About Details") },
                label = { Text(text = if (isHeadline) "About Headline" else "About Details") },

                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = font.sp).copy(textDirection = TextDirection.Content),
                colors = theme.outlinedTextFieldStyle(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                trailingIcon = {
                    IconButton(
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                            //.align(Alignment.CenterVertically)
                            .padding(5.dp)
                            .background(
                                color = theme.background.darker(0.3F),
                                shape = CircleShape
                            ),
                        onClick = {
                            if (i == post.content.lastIndex) {
                                makeFontDialogVisible()
                                scrollToEnd(post.content.size + 4)
                            } else {
                                removeAboutIndex(i)
                            }
                        }
                    ) {
                        Icon(
                            if (i == post.content.lastIndex) Icons.Filled.Add else Icons.Default.Delete,
                            contentDescription = "",
                        )
                    }
                }
            )
        }
        if (isFontDialogVisible) {
            item {
                PostContentCreator {
                    addAbout(it)
                }
            }
        }
    }
}


@Composable
fun PostContentCreator(
    theme: Theme = koinInject(),
    addContentFont: (Int) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .wrapContentSize()
                .widthIn(0.dp, 300.dp)
                .padding(5.dp),
            shape = RoundedCornerShape(20.dp),
            color = theme.backDarkSec,
            tonalElevation = 2.dp,
            shadowElevation = 2.dp,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                TextButton(
                    onClick = {
                        addContentFont(DETAILS_FONT)
                    },
                    modifier = Modifier.padding(5.dp),
                ) {
                    Text("Small Font", color = theme.textColor)
                }
                HorizontalDivider(
                    color = Color.Gray, modifier = Modifier
                        .height(30.dp)
                        .width(1.dp)
                )
                TextButton(
                    onClick = {
                        addContentFont(HEADLINE_FONT)
                    },
                    modifier = Modifier.padding(5.dp),
                ) {
                    Text("Big Font", color = theme.textColor)
                }
            }
        }
    }
}

