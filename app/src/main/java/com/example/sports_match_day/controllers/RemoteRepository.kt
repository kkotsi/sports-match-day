package com.example.sports_match_day.controllers

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.sports_match_day.controllers.paging.MatchesDataSource
import com.example.sports_match_day.firebase.FirebaseRepository
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Participant
import org.koin.core.KoinComponent
import org.koin.core.get
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId

/**
 * Created by Kristo on 07-Mar-21
 */
class RemoteRepositoryImpl(
    private val firebaseRepository: FirebaseRepository,
    private val decoupleAdapter: DecoupleAdapter
) : RemoteRepository,
    KoinComponent {

    override fun getMatches(): Pager<Int, Match> {
        return Pager(PagingConfig(pageSize = MatchesDataSource.PAGE_SIZE, prefetchDistance = 4)) {
            get<MatchesDataSource>()
        }
    }

    override suspend fun removeMatch(match: Match): Boolean {
        firebaseRepository.removeMatch(match.id)
        return true
    }

    override suspend fun removeMatchBySport(sportId: Int): MutableList<Match> {
        return decoupleAdapter.toMatches(firebaseRepository.removeMatchesBySport(sportId) ?: mutableListOf())
    }

    override suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>
    ): Int {
        return firebaseRepository.addMatch(
            city,
            country,
            sportId,
            stadium,
            date.atZone(ZoneId.systemDefault()).toEpochSecond(),
            participants
        )
    }

    override suspend fun getMatch(matchId: Int): Match? {
        return decoupleAdapter.toMatch(firebaseRepository.getMatch(matchId))
    }

    override suspend fun getAllMatches(): List<Match> {
        return decoupleAdapter.toMatches(firebaseRepository.getMatches())
    }

    override suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: LocalDateTime,
        participants: List<Participant>
    ): Boolean {
        firebaseRepository.updateMatch(
            id,
            city,
            country,
            stadium,
            sportId,
            date.atZone(ZoneId.systemDefault()).toEpochSecond(),
            participants
        )
        return true
    }

    override suspend fun removeMatchByAthlete(matchIds: List<Int>): List<Match>{
        return decoupleAdapter.toMatches(firebaseRepository.removeMatchByAthlete(matchIds))
    }

    override suspend fun removeMatchBySquad(matchIds: List<Int>): List<Match> {
        return decoupleAdapter.toMatches(firebaseRepository.removeMatchBySquad(matchIds))
    }
}

interface RemoteRepository {
    fun getMatches(): Pager<Int, Match>

    suspend fun removeMatch(match: Match): Boolean
    suspend fun removeMatchBySport(sportId: Int): MutableList<Match>

    suspend fun addMatch(
        city: String,
        country: String,
        sportId: Int,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>
    ): Int

    suspend fun getMatch(matchId: Int): Match?
    suspend fun getAllMatches(): List<Match>

    suspend fun updateMatch(
        id: Int,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        date: LocalDateTime,
        participants: List<Participant>
    ): Boolean

    suspend fun removeMatchByAthlete(matchIds: List<Int>): List<Match>
    suspend fun removeMatchBySquad(matchIds: List<Int>): List<Match>
}