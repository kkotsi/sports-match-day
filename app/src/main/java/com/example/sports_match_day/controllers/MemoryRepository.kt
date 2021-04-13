package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.*
import com.example.sports_match_day.utils.doToAll
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class MemoryRepository() {

    var matches = mutableListOf<Match>()
    var sports = mutableListOf<Sport>()
    var squads = mutableListOf<Squad>()
    var athletes = mutableListOf<Athlete>()

    fun updateMatch(
        id: Int,
        city: String,
        country: Locale,
        stadium: String,
        sport: Sport,
        date: LocalDateTime,
        participants: List<Participant>
    ) {
        matches.find { it.id == id }?.let {
            it.city = city
            it.country = country
            it.stadium = stadium
            it.sport = sport
            it.date = date
            it.participants.clear()
            it.participants.addAll(participants)
        }
    }

    fun updateSport(id: Int, name: String, type: Boolean, gender: Boolean, count: Int) {
        val mType = if(type) SportType.SOLO else SportType.TEAM
        val mGender = if(gender) Gender.MALE else Gender.FEMALE

        sports.find { it.id == id }?.let {
            it.name = name
            it.type = mType
            it.gender = mGender
            it.participantCount = count
        }

        squads.doToAll({
            it.sport?.id == id
        }){ squad ->
            squad.sport?.name = name
            squad.sport?.type = mType
            squad.sport?.gender = mGender
            squad.sport?.participantCount = count
        }

        athletes.doToAll({
            it.sport?.id == id
        }){ squad ->
            squad.sport?.name = name
            squad.sport?.type = mType
            squad.sport?.gender = mGender
            squad.sport?.participantCount = count
        }
    }

    fun updateSquad(
        id: Int,
        name: String,
        city: String,
        countryLocale: Locale,
        stadium: String,
        sport: Sport,
        birthday: LocalDateTime,
        gender: Boolean,
        matchIds: MutableList<Int>
    ) {
        val mGender = if(gender) Gender.MALE else Gender.FEMALE
        squads.find { it.id == id }?.let {
            it.name = name
            it.city = city
            it.country = countryLocale
            it.stadium = stadium
            it.sport = sport
            it.birthday = birthday
            it.gender = mGender
            it.matches = matchIds
        }
    }

    fun updateAthlete(
        id: Int,
        name: String,
        city: String,
        countryLocale: Locale,
        gender: Boolean,
        sport: Sport,
        birthday: LocalDateTime,
        matchIds: MutableList<Int>
    ) {
        athletes.find { it.id == id }?.let {
            val mGender = if(gender) Gender.MALE else Gender.FEMALE
            it.name = name
            it.city = city
            it.country = countryLocale
            it.gender = mGender
            it.sport = sport
            it.birthday = birthday
            it.matches = matchIds
        }
    }
}