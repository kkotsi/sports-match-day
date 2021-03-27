package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.*
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
}