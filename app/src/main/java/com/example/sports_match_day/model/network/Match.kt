package com.example.sports_match_day.model.network

import android.location.Address
import com.example.sports_match_day.model.Location
import com.example.sports_match_day.model.Sport
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var date: Long,
    var city: Location,
    var country: String,
    var sportId: Int,
    var participants: List<Participant>
)

class Participant(
    var id: Int,
    var score: Double
)