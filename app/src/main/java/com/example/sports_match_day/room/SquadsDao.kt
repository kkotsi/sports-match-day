package com.example.sports_match_day.room

import androidx.room.*
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Squad

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface SquadsDao {

    @Query("SELECT * FROM squad")
    fun getSquads(): List<Squad>

    @Query("SELECT * FROM squad LIMIT :count OFFSET :_offset")
    fun getSquads(count: Int, _offset: Int ): List<Squad>

    @Insert
    fun insertSquad(squad: Squad)

    @Delete
    fun deleteSquad(squad: Squad)

    @Update
    fun updateSquad(squad: Squad)
}