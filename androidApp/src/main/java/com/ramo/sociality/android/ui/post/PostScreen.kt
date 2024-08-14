package com.ramo.sociality.android.ui.post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.navigation.Screen
import com.ramo.sociality.android.global.ui.BackButtonLess
import com.ramo.sociality.android.global.ui.DotsIndicator
import com.ramo.sociality.android.global.util.imageBuildr
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.koin.compose.koinInject

@Composable
fun PostScreen(screen: () -> Screen.PostRoute?, backPress: suspend () -> Unit, theme: Theme = koinInject()) {
    val list = remember {
        screen()?.postMedia ?: emptyList()
    }
    val isVisible = remember {
        mutableStateOf(true)
    }
    val scope = rememberCoroutineScope()
    val colorStopsUpper = remember {
        arrayOf(1F to theme.backDarkAlpha, 0F to Color.Transparent)
    }
    val colorStopsDown = remember {
        arrayOf(0.0F to theme.backDarkAlpha, 1F to Color.Transparent)
    }
    Scaffold { padding ->
        Column(Modifier.padding(padding)) {
            val pagerState = rememberPagerState(initialPage = screen()?.pos ?: 0, pageCount = { list.size })
            Box(
                Modifier
                    .fillMaxSize()
                    .background(theme.background)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(13.dp))
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(theme.background),
                    ) {
                        coil.compose.SubcomposeAsyncImage(
                            model = LocalContext.current.imageBuildr(list[it].mediaURL),
                            success = { (painter, _) ->
                                Image(
                                    contentScale = ContentScale.Fit,
                                    painter = painter,
                                    contentDescription = "Image",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .zoomable(rememberZoomState(), onTap = {
                                            isVisible.value = !isVisible.value
                                        }),
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
                Row(Modifier.fillMaxSize()) {
                    AnimatedVisibility(modifier = Modifier.fillMaxSize(), visible = isVisible.value) {
                        Column(verticalArrangement = Arrangement.SpaceBetween) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(60.dp)
                                    .background(Brush.verticalGradient(colorStops = colorStopsUpper)),
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
                                    }
                                }
                            }
                            if (list.size > 1) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp)
                                        .background(Brush.verticalGradient(colorStops = colorStopsDown)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Spacer(modifier = Modifier.height(13.dp))
                                    DotsIndicator(
                                        totalDots = pagerState.pageCount,
                                        selectedIndex = pagerState.currentPage,
                                        selectedColor = theme.textHintColor,
                                        unSelectedColor = theme.textHintAlpha
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}