package com.example.sports_match_day.model.network

/**
 * Created by Kristo on 08-Mar-21
 */
class Match(
    var id: Int = 0,
    var date: Long = 0,
    var city: String = "",
    var country: String = "",
    var stadium: String = "",
    var sportId: Int = 0,
    var participants: HashMap<String, HashMap<String, Any>> = hashMapOf()
)

class Participant(
    map: HashMap<String, Any>
) {
    var id: Int = map["participantId"].toString().toInt()
    var score: Double = map["score"].toString().toDouble()
}