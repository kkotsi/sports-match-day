package com.example.sports_match_day.di

import com.example.sports_match_day.firebase.NotificationFactory
import com.example.sports_match_day.firebase.NotificationHandler
import org.koin.dsl.module

/**
 * Created by Kristo on 01-Apr-21
 */
val firebaseModule = module {
    single { NotificationFactory(get()) }
    single { NotificationHandler(get(), get()) }
}