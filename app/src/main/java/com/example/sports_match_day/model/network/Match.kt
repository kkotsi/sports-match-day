package com.example.sports_match_day.model.network

import android.location.Address
import com.example.sports_match_day.model.Sport
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var date: Long,
    var city: Location,
    var country: String,
    var sport: Int,
    var participants: List<Participant>
)

class Location(
    var latitude: Double,
    var longitude: Double
)

class Participant(
    var id: Int,
    var score: Int
)