package com.example.sports_match_day.room

import androidx.room.*
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Sport
import com.example.sports_match_day.room.model.Squad

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface SportsDao {

    @Query("SELECT * FROM sport")
    fun getSports(): List<Sport>

    @Query("SELECT * FROM sport LIMIT :count OFFSET :_offset")
    fun getSport(count: Int, _offset: Int ): List<Sport>

    @Insert
    fun insertSport(sport: Sport)

    @Delete
    fun deleteSport(sport: Sport)

    @Update
    fun updateSport(sport: Sport)
}
