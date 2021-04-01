package com.example.sports_match_day.di

import org.koin.core.module.Module

/**
 * Created by Kristo on 07-Mar-21
 */
val appComponent: List<Module> =
    listOf(
        roomModule,
        controllerModule,
        viewModelModule,
        firebaseModule
    )