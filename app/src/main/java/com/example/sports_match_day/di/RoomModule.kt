package com.example.sports_match_day.di

import androidx.room.Room
import com.example.sports_match_day.room.SportsDatabase
import org.koin.dsl.module

/**
 * Created by Kristo on 07-Mar-21
 */
val roomModule = module() {
    single {
        Room.databaseBuilder(
            get(),
            SportsDatabase::class.java, "cocktails-users"
        ).build()
    }
}