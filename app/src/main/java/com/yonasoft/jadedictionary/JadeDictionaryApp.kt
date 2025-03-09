package com.yonasoft.jadedictionary

import android.app.Application
import com.yonasoft.jadedictionary.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JadeDictionaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JadeDictionaryApp)
            modules(appModule)
        }
    }
}
