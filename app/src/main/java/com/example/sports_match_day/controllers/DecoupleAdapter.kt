package com.example.sports_match_day.controllers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.sports_match_day.model.*
import org.koin.core.KoinComponent
import org.koin.core.get
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*


/**
 * Created by Kristo on 08-Mar-21
 */
class DecoupleAdapter(var context: Context) : KoinComponent {

    suspend fun toMatches(matches: List<com.example.sports_match_day.model.network.Match?>): MutableList<Match> {
        val newMatches = mutableListOf<Match>()
        matches.forEach {
            it?.let {
                newMatches.add(toMatch(it))
            }
        }
        return newMatches
    }

    private suspend fun toMatch(match: com.example.sports_match_day.model.network.Match): Match {
        val date = epochConverter(match.date)
        val city = locationConverter(match.city)
        val country = countryConverter(match.country)

        val participants = mutableListOf<Participant>()

        // Use dependency retrieval instead of constructor to avoid circular dependency
        val coreController = get<CoreController>()
        val sport = coreController.getSport(match.sportId)

        sport?.let {
            match.participants.forEach {
                val participant = com.example.sports_match_day.model.network.Participant(it)
                val athleteOrSquad = if (sport.type == SportType.TEAM) {
                    coreController.getSquad(participant.id)
                } else {
                    coreController.getAthlete(participant.id)
                }
                participants.add(participantsConverter(participant, athleteOrSquad))
            }
        }

        return Match(match.id,date, city, country, sport, participants)
    }

    fun toSports(sports: List<com.example.sports_match_day.room.entities.Sport>): List<Sport> {
        val newSports = mutableListOf<Sport>()
        sports.forEach { roomSport ->
            val sport = toSport(roomSport)
            sport?.let {
                newSports.add(it)
            }
        }
        return newSports
    }

    fun toSport(sport: com.example.sports_match_day.room.entities.Sport?): Sport? {
        sport?.let {
            val type: SportType = if (sport.type) {
                SportType.SOLO
            } else {
                SportType.TEAM
            }

            val gender: Gender = if (sport.gender) {
                Gender.MALE
            } else {
                Gender.FEMALE
            }
            return Sport(sport.id, sport.name, type, gender)
        }
        return null
    }

    suspend fun toAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>): List<Athlete> {
        val newAthletes = mutableListOf<Athlete>()
        athletes.forEach { roomAthlete ->
            val nAthlete = toAthlete(roomAthlete)
            nAthlete?.let {
                newAthletes.add(it)
            }
        }
        return newAthletes
    }

    suspend fun toAthlete(athlete: com.example.sports_match_day.room.entities.Athlete?): Athlete? {
        athlete?.let {
            val birthday = epochConverter(athlete.birthday)
            val city = locationConverter(athlete.city)
            val country = countryConverter(athlete.country)

            val gender = if (athlete.gender) Gender.MALE else Gender.FEMALE

            val coreController = get<CoreController>()
            val sport = coreController.getSport(athlete.sportId) ?: return null

            return Athlete(athlete.id, athlete.name, city, country, sport, birthday, gender)
        }
        return null
    }

    suspend fun toSquads(squads: List<com.example.sports_match_day.room.entities.Squad>): List<Squad> {
        val newSquads = mutableListOf<Squad>()
        squads.forEach { roomSquad ->
            val squad = toSquad(roomSquad)
            squad?.let {
                newSquads.add(it)
            }
        }
        return newSquads
    }

    suspend fun toSquad(squad: com.example.sports_match_day.room.entities.Squad?): Squad? {
        squad?.let {
            val birthday = epochConverter(squad.birthday)
            val city = locationConverter(squad.city)
            val country = countryConverter(squad.country)

            val coreController = get<CoreController>()
            val sport = coreController.getSport(squad.sportId) ?: return null

            return Squad(
                squad.id,
                squad.name,
                squad.stadium,
                city,
                country,
                sport,
                birthday
            )
        }
        return null
    }

    private fun participantsConverter(
        participant: com.example.sports_match_day.model.network.Participant,
        contestant: Contestant?
    ): Participant {
        return Participant(contestant, participant.score)
    }

    private fun countryConverter(code: String): Locale {
        return Locale(code)
    }

    private fun locationConverter(location: String): Address? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        val addresses = geoCoder.getFromLocationName(location, 1)
        if (addresses.size > 0)
            return addresses[0]

        return null
    }

    private fun epochConverter(epoch: Long): LocalDateTime {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch), ZoneId.systemDefault())
    }
}