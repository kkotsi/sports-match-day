package com.example.sports_match_day.model

import android.location.Address
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var id: Int,
    var date: LocalDateTime,
    var city: Address?,
    var country: Locale,
    var sport: Sport?,
    var participants: List<Participant>
    )

class Participant(
    var participant: MatchParticipant?,
    var score: Double
)