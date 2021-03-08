package com.example.sports_match_day.room

import androidx.room.*
import com.example.sports_match_day.room.model.Athlete

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface AthletesDao {

    @Query("SELECT * FROM athlete")
    fun getAthletes(): List<Athlete>

    @Query("SELECT * FROM athlete LIMIT :count OFFSET :mOffset")
    fun getAthletes(count: Int, mOffset: Int ): List<Athlete>

    @Insert
    fun insertAthlete(athlete: Athlete)

    @Delete
    fun deleteAthlete(athlete: Athlete)

    @Update
    fun updateAthlete(athlete: Athlete)
}