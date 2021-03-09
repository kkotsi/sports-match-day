package com.example.sports_match_day.model

import android.location.Address
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 07-Mar-21
 * A class that represents an individual athlete
 */
class Athlete(
    var id: Int,
    name: String,
    var city: Address,
    var country: Locale,
    var sportId: Int,
    var birthday: LocalDateTime
): MatchParticipant(name)