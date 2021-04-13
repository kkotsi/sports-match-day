package com.example.sports_match_day.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.sports_match_day.room.entities.Squad

/**
 * Created by Kristo on 07-Mar-21
 */
@Dao
interface SquadsDao {

    @Query("SELECT * FROM squads")
    suspend fun getSquads(): List<Squad>

    @Query("SELECT * FROM squads WHERE squads.sportId = :sportId")
    suspend fun getSquads(sportId: Int): List<Squad>

    @Query("SELECT * FROM squads LIMIT :count OFFSET :mOffset")
    suspend fun getSquads(count: Int, mOffset: Int ): List<Squad>

    @Query("SELECT * FROM squads WHERE squads.id = :id")
    suspend fun getSquad(id: Int): Squad

    @Insert
    suspend fun insertSquad(squad: Squad)

    @Query("DELETE FROM squads WHERE sportId=:sportId")
    suspend fun deleteSquadBySport(sportId: Int)

    @Query("DELETE FROM squads WHERE id=:id")
    suspend fun deleteSquad(id: Int)

    @Update
    suspend fun updateSquad(squad: Squad)

    @Query("Update squads SET matches=:matches WHERE id=:squadId")
    suspend fun updateSquadMatches(squadId: Int, matches: List<Int>)

    @Query("SELECT COUNT(id) FROM squads")
    suspend fun getCount(): Int
}