package com.example.sports_match_day.controllers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.*
import com.example.sports_match_day.utils.RawManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by Kristo on 08-Mar-21
 */
class CoreControllerImpl(
    private var context: Context,
    private var firebaseRepository: FirebaseRepository,
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

    override fun getMatches(): LiveData<PagedList<Match>> {
        return localRepository.getMatches()
    }

    override suspend fun getAthlete(id: Int): Athlete? {
        return localRepository.getAthlete(id)
    }

    override fun getAthletes(): LiveData<PagedList<Athlete>> {
        return localRepository.getAthletes()
    }

    override fun getSquads(): LiveData<PagedList<Squad>> {
        return localRepository.getSquads()
    }

    override fun getSports(): LiveData<PagedList<Sport>> {
        return localRepository.getSports()
    }

    override suspend fun getAllAthletes(): MutableList<Athlete> {
        return localRepository.getAllAthletes()
    }

    override suspend fun getAllSquads(): MutableList<Squad> {
        return localRepository.getAllSquads()
    }

    override suspend fun getAllSports(): MutableList<Sport> {
        return localRepository.getAllSports()
    }

    override suspend fun getSquad(id: Int): Squad? {
        return localRepository.getSquad(id)
    }

    override suspend fun getSport(id: Int): Sport? {
        return localRepository.getSport(id)
    }

    override suspend fun removeMatch(match: Match) : Boolean{
        memoryRepository.matches.remove(match)
        firebaseRepository.removeMatch(match.id)
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
        city: String,
        country: String,
        sportId: Int,
        date: LocalDateTime,
        participants: List<Participant>
    ) {
        firebaseRepository.addMatch(
            city,
            country,
            sportId,
            date.atZone(ZoneId.systemDefault()).toEpochSecond(),
            participants
        )
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
}

interface CoreController {
    suspend fun loadMatches(matches: MutableLiveData<List<Match>>): Boolean
    suspend fun loadSamples(): Boolean

    fun getMatches(): LiveData<PagedList<Match>>
    fun getAthletes(): LiveData<PagedList<Athlete>>
    fun getSquads(): LiveData<PagedList<Squad>>
    fun getSports(): LiveData<PagedList<Sport>>

    suspend fun getAllAthletes(): MutableList<Athlete>
    suspend fun getAllSquads(): MutableList<Squad>
    suspend fun getAllSports(): MutableList<Sport>

    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSquad(id: Int): Squad?
    suspend fun getSport(id: Int): Sport?

    suspend fun removeMatch(match: Match): Boolean
    suspend fun removeAthlete(athlete: Athlete): Boolean
    suspend fun removeSquad(squad: Squad): Boolean
    suspend fun removeSport(sport: Sport): Boolean

    suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        date: LocalDateTime,
        participants: List<Participant>
    )

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

}