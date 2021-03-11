package com.example.sports_match_day.controllers

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.utils.RawManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs

/**
 * Created by Kristo on 08-Mar-21
 */
class CoreControllerImpl(
    private var context: Context,
    private var firebaseRepository: FirebaseRepository,
    private var memoryRepository: MemoryRepository,
    private var decoupleAdapter: DecoupleAdapter,
    private var localRepository: LocalRepository
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
        if(Prefs.getBoolean(PreferencesKeys.SETUP_SAMPLE_DATA, false)){
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

    override suspend fun getAthlete(id: Int): Athlete? {
        return localRepository.getAthlete(id)
    }

    override fun getAthletes(): LiveData<PagedList<Athlete>> {
        return localRepository.getAthletes()
    }

    override fun getSquads(): LiveData<PagedList<Squad>> {
        return localRepository.getSquads()
    }

    override suspend fun getSquad(id: Int): Squad? {
        return localRepository.getSquad(id)
    }

    override suspend fun getSport(id: Int): Sport? {
        return localRepository.getSport(id)
    }

    override suspend fun removeAthlete(athlete: Athlete) {
        return localRepository.removeAthlete(athlete)
    }

    override suspend fun removeSquad(squad: Squad) {
        return localRepository.removeSquad(squad)
    }
}

interface CoreController {
    suspend fun loadMatches(matches: MutableLiveData<List<Match>>): Boolean
    suspend fun loadSamples(): Boolean

    suspend fun getAthlete(id: Int): Athlete?
    fun getAthletes(): LiveData<PagedList<Athlete>>
    fun getSquads(): LiveData<PagedList<Squad>>

    suspend fun getSquad(id: Int): Squad?
    suspend fun getSport(id: Int): Sport?

    suspend fun removeAthlete(athlete: Athlete)
    suspend fun removeSquad(squad: Squad)
}