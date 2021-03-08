package com.example.sports_match_day.controllers

import com.example.sports_match_day.model.Match

/**
 * Created by Kristo on 08-Mar-21
 */
class MemoryRepository() {
    var matches = mutableListOf<Match>()
}