package com.example.sports_match_day.controllers

import androidx.lifecycle.LiveData
import androidx.paging.Config
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.example.sports_match_day.controllers.paging.AthletesDataSource
import com.example.sports_match_day.controllers.paging.SportsDataSource
import com.example.sports_match_day.controllers.paging.SquadsDataSource
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.room.SportsDatabase

/**
 * Created by Kristo on 08-Mar-21
 */
class LocalRepositoryImpl(
    private var sportsDatabase: SportsDatabase,
    private var decoupleAdapter: DecoupleAdapter,
    private var athletesFactory : AthletesDataSource.Factory,
    private var squadsFactory : SquadsDataSource.Factory,
    private var sportsFactory : SportsDataSource.Factory
) : LocalRepository {

    override fun getAthletes(): LiveData<PagedList<Athlete>> {
        val config = Config(AthletesDataSource.PAGE_SIZE, AthletesDataSource.PAGE_SIZE - 2, false)
        return athletesFactory.toLiveData(config)
    }

    override fun getSports(): LiveData<PagedList<Sport>> {
        val config = Config(SquadsDataSource.PAGE_SIZE, SquadsDataSource.PAGE_SIZE - 2, false)
        return sportsFactory.toLiveData(config)
    }

    override fun getSquads(): LiveData<PagedList<Squad>> {
        val config = Config(SquadsDataSource.PAGE_SIZE, SquadsDataSource.PAGE_SIZE - 2, false)
        return squadsFactory.toLiveData(config)
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

    override suspend fun removeAthlete(athlete: Athlete) {
        sportsDatabase.athletesDao().deleteAthlete(athlete.id)
    }

    override suspend fun removeSquad(squad: Squad) {
        sportsDatabase.squadsDao().deleteSquad(squad.id)
    }

    override suspend fun removeSport(sport: Sport) {
        sportsDatabase.sportsDao().deleteSport(sport.id)
    }
}

interface LocalRepository {
    fun getAthletes():  LiveData<PagedList<Athlete>>
    fun getSports(): LiveData<PagedList<Sport>>
    fun getSquads(): LiveData<PagedList<Squad>>


    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSport(id: Int): Sport?
    suspend fun getSquad(id: Int): Squad?

    suspend fun setAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>)
    suspend fun setSports(sports: List<com.example.sports_match_day.room.entities.Sport>)
    suspend fun setSquads(squads: List<com.example.sports_match_day.room.entities.Squad>)

    suspend fun removeAthlete(athlete: Athlete)
    suspend fun removeSquad(squad: Squad)
    suspend fun removeSport(sport: Sport)
}