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
    private var athletesFactory: AthletesDataSource.Factory,
    private var squadsFactory: SquadsDataSource.Factory,
    private var sportsFactory: SportsDataSource.Factory,
    private var memoryRepository: MemoryRepository
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

    override suspend fun getAllAthletes(): MutableList<Athlete> {
        val count = sportsDatabase.athletesDao().getCount()
        if (memoryRepository.athletes.size < count) {
            memoryRepository.athletes.addAll(
                decoupleAdapter.toAthletes(
                    sportsDatabase.athletesDao().getAthletes(
                        count - memoryRepository.athletes.size,
                        memoryRepository.athletes.size
                    )
                )
            )
        }
        return memoryRepository.athletes
    }

    override suspend fun getAllSquads(): MutableList<Squad> {
        val count = sportsDatabase.squadsDao().getCount()
        if (memoryRepository.squads.size < count) {
            memoryRepository.squads.addAll(
                decoupleAdapter.toSquads(
                    sportsDatabase.squadsDao().getSquads(
                        count - memoryRepository.squads.size,
                        memoryRepository.squads.size
                    )
                )
            )
        }
        return memoryRepository.squads
    }

    override suspend fun getAllSports(): MutableList<Sport> {
        val count = sportsDatabase.sportsDao().getCount()
        if (memoryRepository.sports.size < count) {
            memoryRepository.sports.addAll(
                decoupleAdapter.toSports(
                    sportsDatabase.sportsDao().getSports(
                        count - memoryRepository.sports.size,
                        memoryRepository.sports.size
                    )
                )
            )
        }
        return memoryRepository.sports
    }

    override suspend fun addAthlete(
        name: String,
        city: String,
        country: String,
        gender: Boolean,
        sportId: Int,
        birthday: Long
    ): Boolean {
        val athlete = com.example.sports_match_day.room.entities.Athlete(
            0,
            name,
            city,
            country,
            sportId,
            birthday,
            gender
        )
        sportsDatabase.athletesDao().insertAthlete(athlete)
        return true
    }

    override suspend fun addSquad(
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: Long
    ): Boolean {
        val squad = com.example.sports_match_day.room.entities.Squad(
            0,
            name,
            stadium,
            city,
            country,
            sportId,
            birthday
        )
        sportsDatabase.squadsDao().insertSquad(squad)
        return true
    }

    override suspend fun addSport(name: String, type: Boolean, gender: Boolean): Boolean {

        val sport = com.example.sports_match_day.room.entities.Sport(
            0,
            name,
            type,
            gender
        )
        sportsDatabase.sportsDao().insertSport(sport)
        return true
    }
}

interface LocalRepository {
    fun getAthletes(): LiveData<PagedList<Athlete>>
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

    suspend fun getAllAthletes(): MutableList<Athlete>
    suspend fun getAllSquads(): MutableList<Squad>
    suspend fun getAllSports(): MutableList<Sport>

    suspend fun addAthlete(
        name: String,
        city: String,
        country: String,
        gender: Boolean,
        sportId: Int,
        birthday: Long
    ): Boolean

    suspend fun addSquad(
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: Long
    ): Boolean

    suspend fun addSport(name: String, type: Boolean, gender: Boolean): Boolean
}