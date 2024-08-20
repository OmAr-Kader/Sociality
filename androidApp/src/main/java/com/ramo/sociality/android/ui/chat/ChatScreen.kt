package com.ramo.sociality.android.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.base.darker
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.BackButtonLess
import com.ramo.sociality.android.global.ui.OnLaunchScreenScope
import com.ramo.sociality.android.global.ui.rememberDoneAll
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.data.model.Message
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.util.loggerError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
fun ChatScreen(
    userBase: UserBase,
    screen: () -> Screen.ChatRoute?,
    backPress: suspend () -> Unit,
    theme: Theme = koinInject(),
    viewModel: ChatViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    OnLaunchScreenScope {
        screen()?.also { viewModel.loadChat(userBase.id, it) }
    }
    val scrollState = rememberLazyListState()
    Scaffold { padding ->
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
                    .fillMaxSize()
                    .padding(start = 5.dp, end = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    BackButtonLess(color = theme.textColor) {
                        scope.launch { backPress() }
                    }
                    Spacer(Modifier.width(7.dp))
                    coil.compose.SubcomposeAsyncImage(
                        model = LocalContext.current.imageBuildr(state.chat.chatPic),
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
                            Image(
                                imageVector = rememberProfile(theme.textColor),
                                contentDescription = "Profile Image",
                                modifier = Modifier
                                    .size(40.dp)
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
                    Spacer(Modifier.width(7.dp))
                    Text(text = state.chat.chatLabel, color = theme.textColor, style = MaterialTheme.typography.titleLarge)
                }
                Row {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Default.MoreVert, tint = theme.textColor, contentDescription = "Options")
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Spacer(Modifier.height(60.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(start = 5.dp, end = 5.dp, bottom = 7.dp),
                    state = scrollState,
                    //contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.messages, key = {
                        it.id.toString() + it.readersIds.size.toString()
                    }) { message ->
                        ChatBubble(
                            message = message,
                            theme = theme,
                            showSenderName = state.chat.showSenderName
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter)
                ) {
                    TextField(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        placeholder = { Text("Message...") },
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                                    .align(Alignment.CenterVertically)
                                    .padding(5.dp)
                                    .background(
                                        color = theme.background.darker(0.3F),
                                        shape = CircleShape
                                    ),
                                onClick = {
                                    viewModel.onSend(userBase.id)
                                }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "",
                                )
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background.darker(0.3F),
                            unfocusedContainerColor = MaterialTheme.colorScheme.background.darker(0.3F),
                        ),
                        value = state.chatText,
                        onValueChange = viewModel::onTextChanged,
                        singleLine = false
                    )
                }
            }
        }
    }
}


@Composable
fun ChatBubble(
    message: Message,
    theme: Theme,
    showSenderName: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .defaultMinSize(minWidth = 200.dp)
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = if (message.isSender) 40.dp else 8.dp,
                    end = if (message.isSender) 8.dp else 40.dp
                )
                .align(if (message.isSender) Alignment.End else Alignment.Start)
        ) {
            if (showSenderName && !message.isSender) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.titleSmall,
                    color = theme.textHintColor
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = if (message.isSender) theme.backgroundPrimary else theme.background.darker(
                            0.3F
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(8.dp)
            ) {
                Column {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Content),
                        color = theme.textColor
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = theme.textGrayColor,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        if (message.isSender) {
                            Icon(
                                modifier = Modifier.height(20.dp),
                                imageVector = rememberDoneAll(), // if (message.isSeen) rememberDoneAll() else rememberDone()
                                contentDescription = if (message.isSeen) "Seen" else "Sent",
                                tint = if (message.isSeen) Color.Blue else theme.textGrayColor
                            )
                        }
                    }
                }
            }
        }
    }
}