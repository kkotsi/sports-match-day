package com.example.sports_match_day.di

import com.example.sports_match_day.controllers.*
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.firebase.FirebaseRepositoryImpl
import org.koin.dsl.module

/**
 * Created by Kristo on 07-Mar-21
 */
val controllerModule = module {
    single<CoreController> { CoreControllerImpl(get(),get(),get(), get(), get()) }
    single<FirebaseRepository> { FirebaseRepositoryImpl() }
    single { MemoryRepository() }
    single { DecoupleAdapter(get()) }
    single<LocalRepository> { LocalRepositoryImpl(get(),get(), get(), get())}
}