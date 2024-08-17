package com.ramo.sociality.android.ui.common

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.base.outlinedTextFieldStyle
import com.ramo.sociality.android.global.ui.SheetBottomTitle
import com.ramo.sociality.android.global.ui.rememberComment
import com.ramo.sociality.android.global.ui.rememberProfile
import com.ramo.sociality.android.global.util.imageBuildr
import com.ramo.sociality.android.global.base.marge
import com.ramo.sociality.data.model.Comment
import com.ramo.sociality.data.model.MemeLord
import com.ramo.sociality.data.model.PostContent
import com.ramo.sociality.global.util.loggerError
import com.skydoves.flexible.bottomsheet.material3.FlexibleBottomSheet
import com.skydoves.flexible.core.FlexibleSheetSize
import com.skydoves.flexible.core.rememberFlexibleBottomSheetState
import org.koin.compose.koinInject

@Composable
fun LazyItemScope.PostItem(
    meme: MemeLord,
    theme: Theme,
    navigateToUser: () -> Unit,
    navigateToImage: (Int) -> Unit,
    onLikeClicked: () -> Unit,
    onCommentClicked: () -> Unit,
    onShareClicked: () -> Unit
) {
    val state = rememberLazyListState()
    val contentPadding = remember {
        PaddingValues(
            start = 15.dp,
            top = 5.dp,
            end = 15.dp,
            bottom = 5.dp
        )
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .animateItem(spring(stiffness = Spring.StiffnessMediumLow), null, null),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = theme.backgroundPrimary),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                .padding(8.dp)
                .clickable(onClick = navigateToUser)) {
                coil.compose.SubcomposeAsyncImage(
                    model = LocalContext.current.imageBuildr(meme.user.profilePicture),
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
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = meme.user.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                    color = theme.textColor
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            PostContentScrollable(meme.post.content, theme)
            if (meme.post.isHaveMedia) {
                LazyRow(Modifier.fillMaxWidth().padding(start = 10.dp, end = 10.dp).height(200.dp).clip(RoundedCornerShape(20.dp)), state = state, horizontalArrangement = Arrangement.SpaceBetween) {
                    itemsIndexed(meme.post.postMedia) { i, it ->
                        coil.compose.SubcomposeAsyncImage(
                            model = LocalContext.current.imageBuildr(it.mediaURL),
                            success = { (painter, _) ->
                                Image(
                                    painter = painter,
                                    contentDescription = "Post Image",
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .height(200.dp)
                                        .clickable(onClick = { navigateToImage(i) }),
                                    contentScale = ContentScale.Fit
                                )
                            },
                            onError = {
                            },
                            contentScale = ContentScale.Crop,
                            filterQuality = FilterQuality.None,
                            contentDescription = "Image"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${meme.likes.size} likes", style = MaterialTheme.typography.bodySmall, color = theme.textHintColor)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "${meme.comments.size} comments", style = MaterialTheme.typography.bodySmall, color = theme.textHintColor)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onLikeClicked,
                    contentPadding = contentPadding,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Filled.Favorite, modifier = Modifier.height(18.dp), contentDescription = "Like", tint = if (meme.isLiked) Color.Red else Color.Red.marge(Color.DarkGray, 0.6F))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Like", color = theme.textColor, fontSize = 13.sp, maxLines = 1, minLines = 1)
                }
                Button(
                    onClick = onCommentClicked,
                    contentPadding = contentPadding,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(rememberComment(theme.textColor), modifier = Modifier.height(18.dp), contentDescription = "Comment", tint = Color(162,104,18))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Comment", color = theme.textColor, fontSize = 13.sp, maxLines = 1, minLines = 1)
                }
                Button(
                    onClick = onShareClicked,
                    contentPadding = contentPadding,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Icon(Icons.Filled.Share, modifier = Modifier.height(18.dp), contentDescription = "Share", tint = Color.Green)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Share", color = theme.textColor, fontSize = 13.sp, maxLines = 1, minLines = 1)
                }
            }
        }
    }
}


@Composable
fun CommentBottomSheet(scrollState: LazyListState, memeLord: MemeLord?, commentText: String, onValueComment: (String) -> Unit, hide: () -> Unit, theme: Theme = koinInject(), onComment: (MemeLord) -> Unit) {
    if (memeLord != null) {
        FlexibleBottomSheet(
            onDismissRequest = {
                hide()
            },
            sheetState = rememberFlexibleBottomSheetState(
                skipIntermediatelyExpanded = true,
                flexibleSheetSize = FlexibleSheetSize(
                    fullyExpanded = 0.7F,
                ),
                allowNestedScroll = true,
                isModal = true,
                skipSlightlyExpanded = true,
            ),
            dragHandle = {
                SheetBottomTitle("Comments", theme)
            },
            containerColor = theme.backDark,
            contentColor = theme.textColor,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = commentText,
                        onValueChange = onValueComment,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text(text = "Write a comment...", fontSize = 14.sp) },
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(textDirection = TextDirection.Content),
                        colors = theme.outlinedTextFieldStyle(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        trailingIcon = {
                            IconButton(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .padding(5.dp)
                                    .background(
                                        color = Color.Transparent,
                                        shape = CircleShape
                                    ),
                                onClick = { onComment(memeLord) }
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "",
                                )
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    state = scrollState
                ) {
                    items(memeLord.comments, key = {
                        it.id.toString() + it.userId + it.postId
                    }) { comment ->
                        Column(Modifier.animateItem(spring(stiffness = Spring.StiffnessMediumLow), null, null)) {
                            CommentItem(comment, theme)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    } else return
}


@Composable
fun CommentItem(comment: Comment, theme: Theme) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        coil.compose.SubcomposeAsyncImage(
            model = LocalContext.current.imageBuildr(comment.userImage),
            success = { (painter, _) ->
                Image(
                    painter = painter,
                    contentDescription = null,
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
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = comment.userName, style = MaterialTheme.typography.titleMedium, color = theme.textColor)
            Text(text = comment.content, style = MaterialTheme.typography.bodyMedium, color = theme.textColor)
            Text(text = comment.timestamp, style = MaterialTheme.typography.labelSmall, color = theme.textHintColor)
        }
    }
}


@Composable
fun PostContentScrollable(
    about: List<PostContent>,
    theme: Theme,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = buildAnnotatedString {
                about.forEach {
                    withStyle(style = SpanStyle(fontSize = it.font.sp)) {
                        append(it.text)
                        append("\n")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 20.dp, start = 20.dp),
            color = theme.textColor,
            textAlign = TextAlign.Justify,
            //fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium.copy(textDirection = TextDirection.Content),
        )
    }
}
