package com.example.sports_match_day.controllers

import android.content.Context
import androidx.paging.Pager
import com.example.sports_match_day.model.*
import com.example.sports_match_day.utils.RawManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.google.android.gms.maps.model.LatLng
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
    private var localRepository: LocalRepository,
) : CoreController {

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

    override suspend fun getAllSports(sportType: SportType, gender: Gender): MutableList<Sport> {
        return localRepository.getAllSports(sportType, gender)
    }

    override suspend fun getMatch(id: Int): Match? {
        return remoteRepository.getMatch(id)
    }

    override suspend fun getSquad(id: Int): Squad? {
        return localRepository.getSquad(id)
    }

    override suspend fun getSport(id: Int): Sport? {
        return localRepository.getSport(id)
    }

    private suspend fun removeMatchFromParticipant(match: Match, matchIds: MutableList<Int>) {
        if (match.participants.isEmpty())
            return

        val nonNullContestant = match.participants.find { it.contestant != null }
        nonNullContestant?.let {
            val isAthlete = nonNullContestant.contestant is Athlete
            if (isAthlete) {
                //update athlete
                match.participants.forEach {
                    (it.contestant as? Athlete)?.let {
                        with(it) {
                            matches.removeAll(matchIds)
                            updateAthlete(
                                id,
                                name,
                                city,
                                getCountryCode(),
                                gender == Gender.MALE,
                                sport!!,
                                birthday,
                                matches
                            )
                        }
                    }
                }
            } else {
                match.participants.forEach {
                    (it.contestant as? Squad)?.let {
                        with(it) {
                            matches.removeAll(matchIds)
                            updateSquad(
                                id,
                                name,
                                city,
                                getCountryCode(),
                                stadium,
                                sport!!,
                                birthday,
                                gender == Gender.MALE,
                                matches,
                                stadiumLocation
                            )
                        }
                    }
                }
            }
        }
    }

    override suspend fun removeMatch(match: Match): Boolean {
        remoteRepository.removeMatch(match)
        removeMatchFromParticipant(match, mutableListOf(match.id))
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

    override suspend fun removeSquadBySport(sport: Sport): Boolean {
        memoryRepository.squads.removeAll { it.sport?.id == sport.id }
        return localRepository.removeSquadBySport(sport)
    }

    override suspend fun removeAthleteBySport(sport: Sport): Boolean {
        memoryRepository.athletes.removeAll { it.sport?.id == sport.id }
        return localRepository.removeAthleteBySport(sport)
    }

    override suspend fun addMatch(
        sport: Sport,
        city: String,
        country: String,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ): Boolean {
        participants.forEach {
            if (it.score < 0) it.score = Participant.UNSET_SCORE
        }
        val matchId = remoteRepository.addMatch(
            city,
            country,
            sport.id,
            stadium,
            date,
            participants,
            stadiumLocation
        )

        if (sport.type == SportType.SOLO) {
            //update athlete
            participants.forEach {
                val athlete = it.contestant as Athlete
                athlete.matches.add(matchId)
                updateAthlete(
                    athlete.id,
                    athlete.name,
                    athlete.city,
                    athlete.getCountryCode(),
                    athlete.gender == Gender.MALE,
                    athlete.sport!!,
                    athlete.birthday,
                    athlete.matches
                )
            }
        } else {
            participants.forEach {
                val squad = it.contestant as Squad
                squad.matches.add(matchId)
                updateSquad(
                    squad.id,
                    squad.name,
                    squad.city,
                    squad.getCountryCode(),
                    squad.stadium,
                    squad.sport!!,
                    squad.birthday,
                    squad.gender == Gender.MALE,
                    squad.matches,
                    stadiumLocation
                )
            }
        }
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
        birthday: LocalDateTime,
        gender: Boolean,
        stadiumLocation: LatLng?
    ): Boolean {
        return localRepository.addSquad(
            name,
            city,
            country,
            stadium,
            sportId,
            birthday.atZone(ZoneId.systemDefault()).toEpochSecond(),
            gender,
            stadiumLocation
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
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ): Boolean {
        remoteRepository.updateMatch(id, city, country, stadium, sport.id, date, participants, stadiumLocation)
        val countryLocale = Locale("", country)
        memoryRepository.updateMatch(id, city, countryLocale, stadium, sport, date, participants, stadiumLocation)
        return true
    }

    override suspend fun updateSquad(
        id: Int,
        name: String,
        city: String,
        country: String,
        stadium: String,
        sport: Sport,
        birthday: LocalDateTime,
        gender: Boolean,
        matchIds: MutableList<Int>,
        stadiumLocation: LatLng?
    ): Boolean {
        localRepository.updateSquad(
            id,
            name,
            city,
            country,
            stadium,
            sport.id,
            birthday.atZone(ZoneId.systemDefault()).toEpochSecond(),
            gender,
            matchIds,
            stadiumLocation
        )
        val countryLocale = Locale("", country)
        memoryRepository.updateSquad(
            id,
            name,
            city,
            countryLocale,
            stadium,
            sport,
            birthday,
            gender,
            matchIds,
            stadiumLocation
        )
        return true
    }

    override suspend fun removeMatchBySport(sport: Sport): Boolean {
        memoryRepository.matches.removeAll { it.sport?.id == sport.id }
        val matchesRemoved = remoteRepository.removeMatchBySport(sport.id)
        val matchesRemovedIds = mutableListOf<Int>()
        matchesRemoved.forEach {
            matchesRemovedIds.add(it.id)
        }
        matchesRemoved.forEach {
            removeMatchFromParticipant(it, matchesRemovedIds)
        }
        return true
    }

    override suspend fun removeMatchByAthlete(athlete: Athlete): Boolean {
        memoryRepository.matches.removeAll { athlete.matches.contains(it.id) }
        val matchesRemoved = remoteRepository.removeMatchByAthlete(athlete.matches)

        val matchesRemovedIds = mutableListOf<Int>()
        matchesRemoved.forEach {
            matchesRemovedIds.add(it.id)
        }

        matchesRemoved.forEach {
            removeMatchFromParticipant(it, matchesRemovedIds)
        }

        return true
    }

    override suspend fun removeMatchBySquad(squad: Squad): Boolean {
        memoryRepository.matches.removeAll { squad.matches.contains(it.id) }
        val matchesRemoved = remoteRepository.removeMatchBySquad(squad.matches)

        val matchesRemovedIds = mutableListOf<Int>()
        matchesRemoved.forEach {
            matchesRemovedIds.add(it.id)
        }

        matchesRemoved.forEach {
            removeMatchFromParticipant(it, matchesRemovedIds)
        }

        return true
    }

    override suspend fun updateAthlete(
        id: Int,
        name: String,
        city: String,
        countryCode: String,
        gender: Boolean,
        sport: Sport,
        birthday: LocalDateTime,
        matchIds: MutableList<Int>
    ): Boolean {
        localRepository.updateAthlete(
            id,
            name,
            city,
            countryCode,
            gender,
            sport.id,
            birthday.atZone(ZoneId.systemDefault()).toEpochSecond(),
            matchIds
        )
        val countryLocale = Locale("", countryCode)
        memoryRepository.updateAthlete(
            id,
            name,
            city,
            countryLocale,
            gender,
            sport,
            birthday,
            matchIds
        )
        return true
    }
}

interface CoreController {
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
    suspend fun getAllSports(sportType: SportType, gender: Gender): MutableList<Sport>

    suspend fun getMatch(id: Int): Match?
    suspend fun getAthlete(id: Int): Athlete?
    suspend fun getSquad(id: Int): Squad?
    suspend fun getSport(id: Int): Sport?

    suspend fun removeMatch(match: Match): Boolean
    suspend fun removeAthlete(athlete: Athlete): Boolean
    suspend fun removeSquad(squad: Squad): Boolean
    suspend fun removeSport(sport: Sport): Boolean

    suspend fun removeSquadBySport(sport: Sport): Boolean
    suspend fun removeAthleteBySport(sport: Sport): Boolean
    suspend fun removeMatchBySport(sport: Sport): Boolean

    suspend fun removeMatchByAthlete(athlete: Athlete): Boolean
    suspend fun removeMatchBySquad(squad: Squad): Boolean

    suspend fun addMatch(
        sport: Sport,
        city: String,
        country: String,
        stadium: String,
        date: LocalDateTime,
        participants: List<Participant>,
        stadiumLocation: LatLng?
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
        birthday: LocalDateTime,
        gender: Boolean,
        stadiumLocation: LatLng?
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
        participants: List<Participant>,
        stadiumLocation: LatLng?
    ): Boolean

    suspend fun updateSquad(
        id: Int,
        name: String,
        city: String,
        country: String,
        stadium: String,
        sport: Sport,
        birthday: LocalDateTime,
        gender: Boolean,
        matchIds: MutableList<Int>,
        stadiumLocation: LatLng?
    ): Boolean

    suspend fun updateAthlete(
        id: Int,
        name: String,
        city: String,
        countryCode: String,
        gender: Boolean,
        sport: Sport,
        birthday: LocalDateTime,
        matchIds: MutableList<Int>
    ): Boolean?
}