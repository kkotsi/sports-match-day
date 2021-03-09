package com.example.sports_match_day.controllers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.sports_match_day.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.core.KoinComponent
import org.koin.core.get
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*


/**
 * Created by Kristo on 08-Mar-21
 */
class DecoupleAdapter(var context: Context): KoinComponent {

    suspend fun toMatches(matches: List<com.example.sports_match_day.model.network.Match>): MutableList<Match>{
        val newMatches = mutableListOf<Match>()
        matches.forEach {
            newMatches.add(toMatch(it))
        }
        return newMatches
    }

    suspend fun toMatch(match: com.example.sports_match_day.model.network.Match): Match{
        val date = epochConverter(match.date)
        val city = locationConverter(match.city)
        val country = countryConverter(match.country)

        val participants = mutableListOf<Participant>()

        // Use dependency retrieval instead of constructor to avoid circular dependency
        val localRepository = get<LocalRepository>()
        val sport = localRepository.getSport(match.sportId)

        sport?.let {
            match.participants.forEach {
                val matchParticipant = if (sport.type == SportType.TEAM) {
                    localRepository.getSquad(it.id)
                } else {
                    localRepository.getAthlete(it.id)
                }
                participants.add(participantsConverter(it, matchParticipant))
            }
        }

        return Match(date, city, country, sport, participants)
    }

    fun toSports(sports: List<com.example.sports_match_day.room.entities.Sport>): List<Sport>{
        val newSports = mutableListOf<Sport>()
        sports.forEach {
            newSports.add(toSport(it))
        }
        return newSports
    }

    fun toSport(sport: com.example.sports_match_day.room.entities.Sport): Sport{

        val type: SportType = if(sport.type){
            SportType.SOLO
        }else{
            SportType.TEAM
        }

        val gender: Gender = if(sport.gender){
            Gender.MALE
        }else{
            Gender.FEMALE
        }
        return Sport(sport.id, sport.name, type, gender)
    }

    fun toAthletes(athletes: List<com.example.sports_match_day.room.entities.Athlete>): List<Athlete>{
        val newAthletes = mutableListOf<Athlete>()
        athletes.forEach {
            newAthletes.add(toAthlete(it))
        }
        return newAthletes
    }

    fun toAthlete(athlete: com.example.sports_match_day.room.entities.Athlete): Athlete{

        val cityType = object : TypeToken<Location>() {}.type
        val cityLocation = Gson().fromJson<Location>(athlete.city, cityType)
        val birthday = epochConverter(athlete.birthday)
        val city = locationConverter(cityLocation)
        val country = countryConverter(athlete.country)

        return Athlete(athlete.id, athlete.name, city, country, athlete.sportId, birthday)
    }

    fun toSquads(squads: List<com.example.sports_match_day.room.entities.Squad>): List<Squad>{
        val newSquads = mutableListOf<Squad>()
        squads.forEach {
            newSquads.add(toSquad(it))
        }
        return newSquads
    }

    fun toSquad(squad: com.example.sports_match_day.room.entities.Squad): Squad{

        val cityType = object : TypeToken<Location>() {}.type
        val cityLocation = Gson().fromJson<Location>(squad.city, cityType)
        val birthday = epochConverter(squad.birthday)
        val city = locationConverter(cityLocation)
        val country = countryConverter(squad.country)

        return Squad(squad.id, squad.name, squad.stadium, city, country, squad.sportId, birthday)
    }

    private fun participantsConverter(participant: com.example.sports_match_day.model.network.Participant, matchParticipant: MatchParticipant?): Participant{
        return Participant(matchParticipant, participant.score)
    }

    private fun countryConverter(code: String): Locale{
        return Locale(code)
    }

    private fun locationConverter(location: Location): Address{
        val geoCoder = Geocoder(context, Locale.getDefault())
        return geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]
    }

    private fun epochConverter(epoch: Long): LocalDateTime{
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epoch),ZoneId.systemDefault() )
    }
}