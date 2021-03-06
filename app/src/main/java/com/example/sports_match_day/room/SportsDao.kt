package com.example.sports_match_day.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sports_match_day.room.entities.Sport

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface SportsDao {

    @Query("SELECT * FROM sports")
    suspend fun getSports(): List<Sport>

    @Query("SELECT * FROM sports LIMIT :count OFFSET :mOffset")
    suspend fun getSports(count: Int, mOffset: Int): List<Sport>

    @Query("SELECT * FROM sports WHERE sports.gender = :gender AND sports.type = :type")
    suspend fun getSports(type: Boolean, gender: Boolean): List<Sport>

    @Query("SELECT * FROM sports WHERE sports.id = :id")
    suspend fun getSport(id: Int): Sport?

    @Insert
    suspend fun insertSport(sport: Sport)

    @Query("DELETE FROM sports WHERE id=:id")
    suspend fun deleteSport(id: Int)

    @Update
    suspend fun updateSport(sport: Sport)

    @Query("SELECT COUNT(id) FROM sports")
    suspend fun getCount(): Int
}
