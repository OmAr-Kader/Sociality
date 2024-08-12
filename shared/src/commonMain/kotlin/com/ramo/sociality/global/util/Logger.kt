package com.ramo.sociality.global.util

fun logger(tag: String = "", error: String) {
    com.ramo.sociality.di.getKoinInstance<org.lighthousegames.logging.KmLog>().w(tag = "==> $tag") { error }
}

fun loggerError(tag: String = "", error: String) {
    com.ramo.sociality.di.getKoinInstance<org.lighthousegames.logging.KmLog>().w(tag = "==> $tag") { error }
}
