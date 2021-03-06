package com.example.sports_match_day.model

import com.google.android.gms.maps.model.LatLng
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var id: Int,
    var date: LocalDateTime,
    var city: String,
    var country: Locale,
    var stadium: String,
    var sport: Sport?,
    var participants: MutableList<Participant>,
    var stadiumLocation: LatLng?
    ){

    fun getCountryCode(): String{
        return country.toString().replaceFirst("_","")
    }
}

class Participant(
    var contestant: Contestant?,
    var score: Double
){
    companion object{
        //If the score hasn't be decided yet.
        const val UNSET_SCORE = -1.0
    }
}