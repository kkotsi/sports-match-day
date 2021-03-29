package com.example.sports_match_day.model

import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 07-Mar-21
 * A class that represents an individual squad
 */
class Squad(
    id: Int,
    name: String,
    var stadium: String,
    var city: String,
    var country: Locale,
    var sport: Sport?,
    var birthday: LocalDateTime,
    var gender: Gender
): Contestant(name, id){
    fun getCountryCode(): String{
        return country.toString().replaceFirst("_","")
    }
}