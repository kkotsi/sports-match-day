package com.example.sports_match_day.di

import android.app.Application
import android.content.ContextWrapper
import com.jakewharton.threetenabp.AndroidThreeTen
import com.pixplicity.easyprefs.library.Prefs
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Kristo on 07-Mar-21
 */
class SportsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        //LocalDateTime
        AndroidThreeTen.init(this)

        //Dependency Injection
        startKoin {
            androidLogger()
            androidContext(this@SportsApplication)
            modules(appComponent)
        }

        //Initialize the Prefs class
        Prefs.Builder()
            .setContext(this)
            .setMode(ContextWrapper.MODE_PRIVATE)
            .setPrefsName(packageName)
            .setUseDefaultSharedPreference(true)
            .build()
    }
}