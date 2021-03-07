package com.example.sports_match_day.di

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by Kristo on 07-Mar-21
 */
class SportsApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SportsApplication)
            modules(appComponent)
        }
    }
}