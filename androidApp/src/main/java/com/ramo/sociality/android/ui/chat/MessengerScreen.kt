package com.ramo.sociality.android.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.BackButtonLess
import com.ramo.sociality.android.global.ui.OnLaunchScreenScope
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.data.model.Chat
import com.ramo.sociality.data.model.UserBase
import com.ramo.sociality.global.base.CHAT_SCREEN_ROUTE
import com.ramo.sociality.global.util.loggerError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun MessengerScreen(
    userBase: UserBase,
    navigateToScreen: suspend (Screen, String) -> Unit,
    backPress: suspend () -> Unit,
    viewModel: MessengerViewModel = koinViewModel(),
    theme: Theme = koinInject(),
) {
    val state by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    OnLaunchScreenScope {
        viewModel.loadChat(userBase.id)
    }
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
                    Spacer(Modifier.width(10.dp))
                    Text(text = "Messenger", color = theme.textColor, style = MaterialTheme.typography.titleLarge)
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.chats, key = {
                    it.id.toString() + it.numberOfLastMessages + it.lastMessage
                }) { chat ->
                    ChatItemView(chatItem = chat, theme = theme) {
                        scope.launch { navigateToScreen(Screen.ChatRoute(chat.id, chat), CHAT_SCREEN_ROUTE) }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatItemView(chatItem: Chat, theme: Theme, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
            coil.compose.SubcomposeAsyncImage(
                model = LocalContext.current.imageBuildr(chatItem.chatPic),
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
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f), verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = chatItem.chatLabel,
                color = theme.textColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text(
                    text = chatItem.lastMessageLineLess,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = if (chatItem.numberOfLastMessages == 0) FontWeight.Normal else FontWeight.Bold),
                    color = theme.textHintColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                if(chatItem.numberOfLastMessages != 0) {
                    Text(
                        text = chatItem.numberOfLastMessages.toString(),
                        fontSize = 11.sp,
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp)
                            .background(color = Color(red = 155, green = 0, blue = 0), shape = CircleShape),
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chatItem.timestampLastMessage,
                style = MaterialTheme.typography.labelSmall,
                color = theme.textHintColor,
            )
        }
    }
}
