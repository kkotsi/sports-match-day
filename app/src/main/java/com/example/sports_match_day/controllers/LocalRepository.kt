package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.room.SportsDatabase

/**
 * Created by Kristo on 08-Mar-21
 */
class LocalRepositoryImpl(
    private var sportsDatabase: SportsDatabase,
    private var decoupleAdapter: DecoupleAdapter
) : LocalRepository {

    override suspend fun getAthletes(): List<Athlete> {
        val athletes = sportsDatabase.athletesDao().getAthletes()
        return decoupleAdapter.toAthletes(athletes)
    }

    override suspend fun getSports(): List<Sport> {
        val roomSports = sportsDatabase.sportsDao().getSports()
        return decoupleAdapter.toSports(roomSports)
    }

    override suspend fun getSquads(): List<Squad> {
        val roomSquads = sportsDatabase.squadsDao().getSquads()
        return decoupleAdapter.toSquads(roomSquads)
    }

    override suspend fun getAthlete(id: Int): Athlete? {
        val roomAthlete = sportsDatabase.athletesDao().getAthlete(id)
        return decoupleAdapter.toAthlete(roomAthlete)
    }

    override suspend fun getSport(id: Int): Sport? {
        val roomSport = sportsDatabase.sportsDao().getSport(id)
        return decoupleAdapter.toSport(roomSport)
    }

    override suspend fun getSquad(id: Int): Squad? {
        val roomSquad = sportsDatabase.squadsDao().getSquad(id)
        return decoupleAdapter.toSquad(roomSquad)
    }

    override suspend fun setAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>) {
        athletes.forEach {
            sportsDatabase.athletesDao().insertAthlete(it)
        }
    }

    override suspend fun setSports(sports: List<com.example.sports_match_day.room.entities.Sport>) {
        sports.forEach {
            sportsDatabase.sportsDao().insertSport(it)
        }
    }

    override suspend fun setSquads(squads: List<com.example.sports_match_day.room.entities.Squad>) {
        squads.forEach {
            sportsDatabase.squadsDao().insertSquad(it)
        }
    }
}

interface LocalRepository {
    suspend fun getAthletes(): List<Athlete>
    suspend fun getSports(): List<Sport>
    suspend fun getSquads(): List<Squad>


    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSport(id: Int): Sport?
    suspend fun getSquad(id: Int): Squad?

    suspend fun setAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>)
    suspend fun setSports(sports: List<com.example.sports_match_day.room.entities.Sport>)
    suspend fun setSquads(squads: List<com.example.sports_match_day.room.entities.Squad>)
}