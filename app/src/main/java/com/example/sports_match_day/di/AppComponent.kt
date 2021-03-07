package com.example.sports_match_day.di

import org.koin.core.module.Module


val appComponent: List<Module> =
    listOf(
        roomModule,
        controllerModule,
        viewModelModule
    )