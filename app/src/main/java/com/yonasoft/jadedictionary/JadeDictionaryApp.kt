package com.yonasoft.jadedictionary

import android.app.Application
import com.github.promeg.pinyinhelper.Pinyin
import com.github.promeg.tinypinyin.lexicons.android.cncity.CnCityDict
import com.yonasoft.jadedictionary.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JadeDictionaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@JadeDictionaryApp)
            modules(appModule)
            Pinyin.init(Pinyin.newConfig().with(CnCityDict.getInstance(this@JadeDictionaryApp)))
        }
    }
}
