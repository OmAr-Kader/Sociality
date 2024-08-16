package com.ramo.sociality.di

import org.koin.core.context.startKoin

fun initKoin(isDebugMode: Boolean) {
    startKoin {
        modules(appModule(isDebugMode))
    }
}