package com.example.sports_match_day.model

import android.location.Address
import android.location.Location
import org.threeten.bp.LocalDateTime
import java.io.Serializable
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var date: LocalDateTime,
    var city: Address,
    var country: Locale,
    var sport: Sport?,
    var participants: List<Participant>
    )

class Participant(
    var participant: MatchParticipant?,
    var score: Double
)