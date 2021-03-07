package com.example.sports_match_day.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Squad

/**
 * Created by Kristo on 07-Mar-21
 */

@Database(
    entities = [Athlete::class, Squad::class],
    version = 1)
@TypeConverters(ListConverter::class)
abstract class SportsDatabase : RoomDatabase() {
    abstract fun sportsDao(): SportsDao
}
