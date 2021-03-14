package com.example.sports_match_day.room

import androidx.room.*
import com.example.sports_match_day.room.entities.Athlete

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface AthletesDao {

    @Query("SELECT * FROM athletes")
    suspend fun getAthletes(): List<Athlete>

    @Query("SELECT * FROM athletes LIMIT :count OFFSET :mOffset")
    suspend fun getAthletes(count: Int, mOffset: Int ): List<Athlete>

    @Query("SELECT * FROM athletes WHERE athletes.id = :id")
    suspend fun getAthlete(id: Int): Athlete

    @Insert
    suspend fun insertAthlete(athlete: Athlete)

    @Query("DELETE FROM athletes WHERE id=:id")
    suspend fun deleteAthlete(id: Int)

    @Delete
    suspend fun deleteAthlete(athlete: Athlete)

    @Update
    suspend fun updateAthlete(athlete: Athlete)

    @Query("SELECT COUNT(id) FROM athletes")
    suspend fun getCount(): Int
}