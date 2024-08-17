package com.ramo.sociality.android.global.base

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Theme(
    val isDarkMode: Boolean,
    val isDarkStatusBarText: Boolean,
    val primary: Color,
    val secondary: Color,
    val background: Color,
    val backDark: Color,
    val backDarkSec: Color,
    val backgroundPrimary: Color,
    val backGreyTrans: Color,
    val textColor: Color,
    val textForPrimaryColor: Color,
    val textGrayColor: Color,
    val error: Color,
    val textHintColor: Color,
    val pri: Color
) {
    val priAlpha: Color = pri.copy(alpha = 0.14F)
    val textHintAlpha = textHintColor.copy(alpha = 0.5F)
    val backDarkAlpha = backDark.copy(alpha = 0.5F)
}

fun generateTheme(isDarkMode: Boolean): Theme {
    /*
    self.background = Color(red: 31 / 255, green: 31 / 255, blue: 31 / 255)
            self.backDark = Color(red: 50 / 255, green: 50 / 255, blue: 50 / 255)
            self.backDarkSec = Color(red: 100 / 255, green: 100 / 255, blue: 100 / 255)


            self.background = Color.white
            self.backDark = Color(red: 230 / 255, green: 230 / 255, blue: 230 / 255)
            self.backDarkSec = Color(red: 200 / 255, green: 200 / 255, blue: 200 / 255)*/
    return if (isDarkMode) {
        Theme(
            isDarkMode = true,
            isDarkStatusBarText = false,
            primary = Color(red = 109, green = 157, blue = 241),
            secondary = Color(red = 65, green = 130, blue = 237),
            background = Color(red = 31, green = 31, blue = 31, alpha = 255),
            backDark = Color(50, 50, 25),
            backDarkSec = Color(100, 100, 100),
            backgroundPrimary = Color(red = 31, green = 31, blue = 31, alpha = 255).darker(),
            backGreyTrans = Color(85, 85, 85, 85),
            textColor = Color.White,
            textForPrimaryColor = Color.Black,
            textGrayColor = Color(143, 143, 143),
            error = Color(red = 255, green = 21, blue = 21),
            textHintColor = Color(red = 204, green = 204, blue = 204),
            pri = Color(102, 158, 255)
        )
    } else {
        Theme(
            isDarkMode = false,
            isDarkStatusBarText = true,
            primary = Color(red = 109, green = 157, blue = 241),
            secondary = Color(red = 65, green = 130, blue = 237),
            background = Color.White,
            backDark = Color(230, 230, 230),
            backDarkSec = Color(200, 200, 200),
            backgroundPrimary = Color.White.darker(),
            backGreyTrans = Color(170, 170, 170, 85),
            textColor = Color.Black,
            textForPrimaryColor = Color.Black,
            textGrayColor = Color(112, 112, 112),
            error = Color(red = 155, green = 0, blue = 0),
            textHintColor = Color(red = 68, green = 68, blue = 68),
            pri = Color(102, 158, 255)
        )
    }
}


@Composable
fun MyApplicationTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val colors = if (isSystemInDarkTheme()) {
        darkColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            onBackground = theme.textHintColor,
        )
    } else {
        lightColorScheme(
            primary = theme.primary,
            secondary = theme.secondary,
            background = theme.background,
            onBackground = theme.textHintColor,
        )
    }
    val typography = Typography(
        bodyMedium = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(4.dp),
        large = RoundedCornerShape(0.dp)
    )
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
