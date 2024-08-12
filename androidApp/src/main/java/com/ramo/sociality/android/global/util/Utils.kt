package com.ramo.sociality.android.global.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils


fun Color.marge(topColor: Color, ratio: Float = 0.5f): Color {
    if (ratio == 0f) return this
    if (ratio == 1f) return topColor
    val intColor = ColorUtils.blendARGB(toArgb(), topColor.toArgb(), ratio)
    return Color(intColor)
}