package com.example.sports_match_day.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.sports_match_day.room.entities.Athlete
import com.example.sports_match_day.room.entities.Sport
import com.example.sports_match_day.room.entities.Squad

/**
 * Created by Kristo on 07-Mar-21
 */

@Database(
    entities = [
        Athlete::class,
        Squad::class,
        Sport::class],
    version = 1
)
@TypeConverters(ListConverter::class)
abstract class SportsDatabase : RoomDatabase() {
    abstract fun sportsDao(): SportsDao
    abstract fun athletesDao(): AthletesDao
    abstract fun squadsDao(): SquadsDao
}
