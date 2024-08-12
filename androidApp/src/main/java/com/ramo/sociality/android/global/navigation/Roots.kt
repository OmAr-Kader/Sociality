package com.ramo.sociality.android.global.navigation

import com.ramo.sociality.data.model.PostMedia
import kotlin.reflect.full.isSubclassOf

sealed class Screen {

    data class PostRoute(val postMedia: List<PostMedia>, val pos: Int) : Screen()
    data class ChatRoute(val chatId: Long, val chat: com.ramo.sociality.data.model.Chat?) : Screen()
    data class ProfileRoute(val userId: String) : Screen()
}

inline fun <reified T : Screen> List<Screen>.valueOf(): T? = filterIsInstance<T>().firstOrNull()

fun values(): List<Screen> =
    Screen::class.nestedClasses
        .filter { clazz -> clazz.isSubclassOf(Screen::class) }
        .map { clazz -> clazz.objectInstance }
        .filterIsInstance<Screen>()

inline fun <reified T : Screen> MutableList<Screen>.replace(screen: T) = apply {
    this@replace.filterIsInstance<T>().firstOrNull()?.also {
        indexOf(it).let { i ->
            this@replace[i] = screen
        }
    } ?: run {
        this@replace.apply {
            add(screen)
        }
    }
}