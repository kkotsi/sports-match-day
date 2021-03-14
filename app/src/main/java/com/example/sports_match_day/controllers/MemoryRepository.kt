package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad

/**
 * Created by Kristo on 08-Mar-21
 */
class MemoryRepository() {
    var matches = mutableListOf<Match>()
    var sports = mutableListOf<Sport>()
    var squads = mutableListOf<Squad>()
    var athletes = mutableListOf<Athlete>()
}