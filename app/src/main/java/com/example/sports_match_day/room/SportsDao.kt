package com.example.sports_match_day.room

import androidx.room.*
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Squad

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface SportsDao {
    @Query("SELECT * FROM athlete")
    fun getAthletes(): List<Athlete>

    @Query("SELECT * FROM athlete LIMIT :count OFFSET :_offset")
    fun getAthletes(count: Int, _offset: Int ): List<Athlete>

    @Insert
    fun insertAthlete(athlete: Athlete)

    @Delete
    fun deleteAthlete(athlete: Athlete)

    @Update
    fun updateAthlete(athlete: Athlete)


    @Query("SELECT * FROM squad")
    fun getSquads(): List<Squad>

    @Query("SELECT * FROM squad LIMIT :count OFFSET :_offset")
    fun getSquads(count: Int, _offset: Int ): List<Athlete>

    @Insert
    fun insertSquad(athlete: Squad)

    @Delete
    fun deleteSquad(athlete: Squad)

    @Update
    fun updateSquad(athlete: Squad)
}
