package com.example.sports_match_day.controllers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.example.sports_match_day.model.*
import com.example.sports_match_day.model.network.Location
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.util.*


/**
 * Created by Kristo on 08-Mar-21
 */
class DecoupleAdapter(var context: Context) {

    fun toMatch(match: com.example.sports_match_day.model.network.Match): Match{
        val date = epochConverter(match.date)
        val city = locationConverter(match.city)
        val country = countryConverter(match.country)

        val participants = mutableListOf<Participant>()
        match.participants.forEach {
            participants.add(participantsConverter(it))
        }

        val sport = Sport(1, "Football", SportType.TEAM, Gender.FEMALE)
        return Match(date, city, country, sport, participants)
    }

    private fun participantsConverter(participant: com.example.sports_match_day.model.network.Participant): Participant{
        return Participant(participant.id,participant.score)
    }

    private fun countryConverter(code: String): Locale{
        return Locale(code)
    }

    private fun locationConverter(location: Location): Address{
        val geoCoder = Geocoder(context, Locale.getDefault())
        return geoCoder.getFromLocation(location.latitude, location.longitude, 1)[0]
    }

    private fun epochConverter(epoch: Long): LocalDateTime{
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epoch),ZoneId.systemDefault() )
    }
}