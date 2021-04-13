package com.example.sports_match_day.controllers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.sports_match_day.controllers.paging.AthletesDataSource
import com.example.sports_match_day.controllers.paging.SportsDataSource
import com.example.sports_match_day.controllers.paging.SquadsDataSource
import com.example.sports_match_day.model.*
import com.example.sports_match_day.room.SportsDatabase
import com.example.sports_match_day.utils.findAll
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Created by Kristo on 08-Mar-21
 */
class LocalRepositoryImpl(
    private var sportsDatabase: SportsDatabase,
    private var decoupleAdapter: DecoupleAdapter,
    private var memoryRepository: MemoryRepository
) : LocalRepository, KoinComponent {

    override fun getAthletes(): Pager<Int, Athlete> {
        return Pager(PagingConfig(pageSize = AthletesDataSource.PAGE_SIZE, prefetchDistance = 4)) {
            get<AthletesDataSource>()
        }
    }

    override fun getSports(): Pager<Int, Sport> {
        return Pager(PagingConfig(pageSize = SportsDataSource.PAGE_SIZE, prefetchDistance = 4)) {
            get<SportsDataSource>()
        }
    }

    override fun getSquads(): Pager<Int, Squad> {
        return Pager(PagingConfig(pageSize = SquadsDataSource.PAGE_SIZE, prefetchDistance = 4)) {
            get<SquadsDataSource>()
        }
    }

    override suspend fun getAthlete(id: Int): Athlete? {
        val roomAthlete = sportsDatabase.athletesDao().getAthlete(id)
        return decoupleAdapter.toAthlete(roomAthlete)
    }

    override suspend fun getSport(id: Int): Sport? {
        val roomSport = sportsDatabase.sportsDao().getSport(id)
        print("ok")
        if(roomSport == null) return null
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

    override suspend fun removeAthlete(athlete: Athlete): Boolean {
        sportsDatabase.athletesDao().deleteAthlete(athlete.id)
        return true
    }

    override suspend fun removeSquad(squad: Squad): Boolean {
        sportsDatabase.squadsDao().deleteSquad(squad.id)
        return true
    }

    override suspend fun removeSport(sport: Sport): Boolean {
        sportsDatabase.sportsDao().deleteSport(sport.id)
        return true
    }

    override suspend fun removeSquadBySport(sport: Sport): Boolean {
        sportsDatabase.squadsDao().deleteSquadBySport(sport.id)
        return true
    }

    override suspend fun removeAthleteBySport(sport: Sport): Boolean {
        sportsDatabase.athletesDao().deleteAthleteBySport(sport.id)
        return true
    }

    override suspend fun getAllAthletes(sportId: Int): MutableList<Athlete> {
        val count = sportsDatabase.athletesDao().getCount()
        if (memoryRepository.athletes.size < count) {
            return decoupleAdapter.toAthletes(
                sportsDatabase.athletesDao().getAthletes(sportId)
            ).toMutableList()
        }
        return memoryRepository.athletes.filter { it.sport?.id == sportId }.toMutableList()
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

    override suspend fun getAllSquads(sportId: Int): MutableList<Squad> {
        val count = sportsDatabase.squadsDao().getCount()
        if (memoryRepository.squads.size < count) {
            return decoupleAdapter.toSquads(
                sportsDatabase.squadsDao().getSquads(sportId)
            ).toMutableList()
        }
        return memoryRepository.squads.filter { it.sport?.id == sportId }.toMutableList()
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

    override suspend fun getAllSports(sportType: SportType, gender: Gender): MutableList<Sport> {
        val count = sportsDatabase.sportsDao().getCount()
        if (memoryRepository.sports.size < count) {
            return decoupleAdapter.toSports(
                sportsDatabase.sportsDao()
                    .getSports(sportType == SportType.SOLO, gender == Gender.MALE)
            ).toMutableList()
        }
        return memoryRepository.sports.findAll { it.gender == gender && it.type == sportType }
            .toMutableList()
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
            gender,
            mutableListOf()
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
        birthday: Long, gender: Boolean
    ): Boolean {
        val squad = com.example.sports_match_day.room.entities.Squad(
            0,
            name,
            stadium,
            city,
            country,
            sportId,
            birthday,
            gender,
            mutableListOf()
        )
        sportsDatabase.squadsDao().insertSquad(squad)
        return true
    }

    override suspend fun addSport(
        name: String, type: Boolean, gender: Boolean,
        count: Int
    ): Boolean {

        val sport = com.example.sports_match_day.room.entities.Sport(
            0,
            name,
            type,
            gender,
            count
        )
        sportsDatabase.sportsDao().insertSport(sport)
        return true
    }

    override suspend fun updateSport(
        id: Int,
        name: String,
        type: Boolean,
        gender: Boolean,
        count: Int
    ) {
        val sport = com.example.sports_match_day.room.entities.Sport(id, name, type, gender, count)
        sportsDatabase.sportsDao().updateSport(sport)
    }

    override suspend fun updateSquad(id: Int, matchIds: MutableList<Int>) {
        sportsDatabase.squadsDao().updateSquadMatches(id,matchIds)
    }

    override suspend fun updateSquad(
        id: Int,
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: Long,
        gender: Boolean,
        matchIds: MutableList<Int>
    ) {
        val squad = com.example.sports_match_day.room.entities.Squad(
            id,
            name,
            stadium,
            city,
            country,
            sportId,
            birthday,
            gender,
            matchIds
        )
        sportsDatabase.squadsDao().updateSquad(squad)
    }

    override suspend fun updateAthlete(id: Int, matches: MutableList<Int>) {
        sportsDatabase.athletesDao().updateAthleteMatches(id,matches)
    }

    override suspend fun updateAthlete(
        id: Int,
        name: String,
        city: String,
        countryCode: String,
        gender: Boolean,
        sportId: Int,
        birthday: Long,
        matchIds: MutableList<Int>
    ) {
        val athlete = com.example.sports_match_day.room.entities.Athlete(
            id,
            name,
            city,
            countryCode,
            sportId,
            birthday,
            gender,
            matchIds
        )
        sportsDatabase.athletesDao().updateAthlete(athlete)
    }
}

interface LocalRepository {
    suspend fun getAllAthletes(sportId: Int): MutableList<Athlete>
    suspend fun getAllSquads(sportId: Int): MutableList<Squad>

    fun getAthletes(): Pager<Int, Athlete>
    fun getSports(): Pager<Int, Sport>
    fun getSquads(): Pager<Int, Squad>

    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSport(id: Int): Sport?
    suspend fun getSquad(id: Int): Squad?

    suspend fun setAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>)
    suspend fun setSports(sports: List<com.example.sports_match_day.room.entities.Sport>)
    suspend fun setSquads(squads: List<com.example.sports_match_day.room.entities.Squad>)

    suspend fun removeAthlete(athlete: Athlete): Boolean
    suspend fun removeSquad(squad: Squad): Boolean
    suspend fun removeSport(sport: Sport): Boolean

    suspend fun removeSquadBySport(sport: Sport): Boolean
    suspend fun removeAthleteBySport(sport: Sport): Boolean

    suspend fun getAllAthletes(): MutableList<Athlete>
    suspend fun getAllSquads(): MutableList<Squad>
    suspend fun getAllSports(): MutableList<Sport>
    suspend fun getAllSports(sportType: SportType, gender: Gender): MutableList<Sport>

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
        birthday: Long, gender: Boolean
    ): Boolean

    suspend fun addSport(
        name: String, type: Boolean, gender: Boolean,
        count: Int
    ): Boolean

    suspend fun updateSport(id: Int, name: String, type: Boolean, gender: Boolean, count: Int)

    suspend fun updateSquad(
        id: Int,
        matchIds: MutableList<Int>
    )

    suspend fun updateSquad(
        id: Int,
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: Long,
        gender: Boolean,
        matchIds: MutableList<Int>
    )
    suspend fun updateAthlete(
        id: Int,
        matches: MutableList<Int>
    )

    suspend fun updateAthlete(
        id: Int,
        name: String,
        city: String,
        countryCode: String,
        gender: Boolean,
        sportId: Int,
        birthday: Long,
        matchIds: MutableList<Int>
    )
}