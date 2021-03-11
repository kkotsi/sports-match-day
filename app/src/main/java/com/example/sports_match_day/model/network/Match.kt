package com.example.sports_match_day.model.network

/**
 * Created by Kristo on 08-Mar-21
 */
class Match (
    var date: Long,
    var city: String,
    var country: String,
    var sportId: Int,
    var participants: List<Participant>
)

class Participant(
    var id: Int,
    var score: Double
)