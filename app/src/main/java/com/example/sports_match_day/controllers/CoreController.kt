package com.example.sports_match_day.controllers

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import com.example.sports_match_day.model.*
import com.example.sports_match_day.utils.RawManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class CoreControllerImpl(
    private var context: Context,
    private var remoteRepository: RemoteRepository,
    private var memoryRepository: MemoryRepository,
    private var decoupleAdapter: DecoupleAdapter,
    private var localRepository: LocalRepository,
) : CoreController {

    override suspend fun loadMatches(matches: MutableLiveData<List<Match>>): Boolean {
        val netMatches: List<com.example.sports_match_day.model.network.Match> =
            RawManager.getMatchesRaw(context)

        val mMatches = decoupleAdapter.toMatches(netMatches)
        memoryRepository.matches = mMatches
        matches.value = mMatches

        return true
    }

    override suspend fun loadSamples(): Boolean {
        if (Prefs.getBoolean(PreferencesKeys.SETUP_SAMPLE_DATA, false)) {
            return true
        }

        val sampleSports =
            RawManager.getSportsRaw(context)

        val sampleSquads =
            RawManager.getSquadsRaw(context)

        val sampleAthletes =
            RawManager.getAthletesRaw(context)

        localRepository.setSports(sampleSports)
        localRepository.setAthletes(sampleAthletes)
        localRepository.setSquads(sampleSquads)

        Prefs.putBoolean(PreferencesKeys.SETUP_SAMPLE_DATA, true)
        return true
    }

    override fun getMatchEvents(): List<LocalDateTime> {
        val returned = mutableListOf<LocalDateTime>()
        memoryRepository.matches.forEach {
            returned.add(it.date)
        }
        return returned
    }

    override fun getMatches(): Pager<Int, Match> {
        return remoteRepository.getMatches()
    }

    override suspend fun getAthlete(id: Int): Athlete? {
        return localRepository.getAthlete(id)
    }

    override fun getAthletes(): Pager<Int, Athlete> {
        return localRepository.getAthletes()
    }

    override fun getSquads(): Pager<Int, Squad> {
        return localRepository.getSquads()
    }

    override fun getSports(): Pager<Int, Sport> {
        return localRepository.getSports()
    }

    override suspend fun getAllAthletes(sportId: Int): MutableList<Athlete> {
        return localRepository.getAllAthletes(sportId)
    }

    override suspend fun getAllAthletes(): MutableList<Athlete> {
        return localRepository.getAllAthletes()
    }

    override suspend fun getAllSquads(sportId: Int): MutableList<Squad> {
        return localRepository.getAllSquads(sportId)
    }

    override suspend fun getAllSquads(): MutableList<Squad> {
        return localRepository.getAllSquads()
    }

    override suspend fun getAllSports(): MutableList<Sport> {
        return localRepository.getAllSports()
    }

    override suspend fun getMatch(matchId: Int): Match? {
        return remoteRepository.getMatch(matchId)
    }

    override suspend fun getSquad(id: Int): Squad? {
        return localRepository.getSquad(id)
    }

    override suspend fun getSport(id: Int): Sport? {
        return localRepository.getSport(id)
    }

    override suspend fun removeMatch(match: Match): Boolean {
        remoteRepository.removeMatch(match)
        memoryRepository.matches.remove(match)
        return true
    }

    override suspend fun removeAthlete(athlete: Athlete): Boolean {
        memoryRepository.athletes.remove(athlete)
        return localRepository.removeAthlete(athlete)
    }

    override suspend fun removeSquad(squad: Squad): Boolean {
        memoryRepository.squads.remove(squad)
        return localRepository.removeSquad(squad)
    }

    override suspend fun removeSport(sport: Sport): Boolean {
        memoryRepository.sports.remove(sport)
        return localRepository.removeSport(sport)
    }

    override suspend fun addMatch(
        sportId: Int,
        city: String,
        country: String,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>
    ): Boolean {
        participants.forEach {
            if (it.score < 0) it.score = Participant.UNSET_SCORE
        }
        remoteRepository.addMatch(
            city,
            country,
            sportId,
            stadium,
            date,
            participants
        )
        return true
    }

    override suspend fun addAthlete(
        name: String,
        city: String,
        country: String,
        gender: Boolean,
        sportId: Int,
        birthday: LocalDateTime
    ): Boolean {
        return localRepository.addAthlete(
            name,
            city,
            country,
            gender,
            sportId,
            birthday.atZone(ZoneId.systemDefault()).toEpochSecond()
        )
    }

    override suspend fun addSquad(
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: LocalDateTime
    ): Boolean {
        return localRepository.addSquad(
            name,
            city,
            country,
            stadium,
            sportId,
            birthday.atZone(ZoneId.systemDefault()).toEpochSecond()
        )
    }

    override suspend fun addSport(
        name: String, type: Boolean, gender: Boolean,
        count: Int
    ): Boolean {
        return localRepository.addSport(name, type, gender, count)
    }

    override suspend fun updateSport(
        id: Int,
        name: String,
        type: Boolean,
        gender: Boolean,
        count: Int
    ): Boolean {
        localRepository.updateSport(id, name, type, gender, count)
        memoryRepository.updateSport(id, name, type, gender, count)
        return true
    }

    override suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sport: Sport,
        date: LocalDateTime,
        participants: List<Participant>
    ): Boolean {
        remoteRepository.updateMatch(id, city, country, stadium, sport.id, date, participants)
        val countryLocale = Locale("",country)
        memoryRepository.updateMatch(id, city, countryLocale, stadium, sport, date, participants)
        return true
    }
}

interface CoreController {
    suspend fun loadMatches(matches: MutableLiveData<List<Match>>): Boolean
    suspend fun loadSamples(): Boolean
    fun getMatchEvents(): List<LocalDateTime>

    fun getMatches(): Pager<Int, Match>
    fun getAthletes(): Pager<Int, Athlete>
    fun getSquads(): Pager<Int, Squad>
    fun getSports(): Pager<Int, Sport>

    suspend fun getAllAthletes(sportId: Int): MutableList<Athlete>
    suspend fun getAllSquads(sportId: Int): MutableList<Squad>

    suspend fun getAllAthletes(): MutableList<Athlete>
    suspend fun getAllSquads(): MutableList<Squad>
    suspend fun getAllSports(): MutableList<Sport>

    suspend fun getMatch(matchId: Int): Match?
    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSquad(id: Int): Squad?
    suspend fun getSport(id: Int): Sport?

    suspend fun removeMatch(match: Match): Boolean
    suspend fun removeAthlete(athlete: Athlete): Boolean
    suspend fun removeSquad(squad: Squad): Boolean
    suspend fun removeSport(sport: Sport): Boolean

    suspend fun addMatch(
        sportId: Int,
        city: String,
        country: String,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>
    ): Boolean

    suspend fun addAthlete(
        name: String,
        city: String,
        country: String,
        gender: Boolean,
        sportId: Int,
        birthday: LocalDateTime
    ): Boolean

    suspend fun addSquad(
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: LocalDateTime
    ): Boolean

    suspend fun addSport(
        name: String, type: Boolean, gender: Boolean,
        count: Int
    ): Boolean

    suspend fun updateSport(
        id: Int, name: String, type: Boolean, gender: Boolean,
        count: Int
    ): Boolean

    suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sport: Sport,
        date: LocalDateTime,
        participants: List<Participant>): Boolean
}