package com.ramo.sociality.android

import android.app.Application
import com.ramo.sociality.android.global.base.Theme
import com.ramo.sociality.android.global.base.generateTheme
import com.ramo.sociality.android.global.util.isDarkMode
import com.ramo.sociality.android.ui.chat.ChatViewModel
import com.ramo.sociality.android.ui.chat.MessengerViewModel
import com.ramo.sociality.android.ui.home.HomeViewModel
import com.ramo.sociality.android.ui.post.PostCreatorViewModel
import com.ramo.sociality.android.ui.profile.ProfileViewModel
import com.ramo.sociality.android.ui.profile.SearchViewModel
import com.ramo.sociality.android.ui.sign.AuthViewModel
import com.ramo.sociality.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(appModule(BuildConfig.DEBUG) + module {
                single<Theme> { generateTheme(isDarkMode = isDarkMode) }
                viewModel { AppViewModel(get()) }
                viewModel { AuthViewModel(get()) }
                single { HomeViewModel(get()) }
                viewModel { PostCreatorViewModel(get()) }
                viewModel { ProfileViewModel(get()) }
                viewModel { SearchViewModel(get()) }
                viewModel { ChatViewModel(get()) }
                viewModel { MessengerViewModel(get()) }
            })
        }
    }
}