package com.example.sports_match_day.ui.dashboard

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.AnalyticsController
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 15-Apr-21
 */
class DashboardViewModel (private var analyticsController: AnalyticsController) : ScopedViewModel() {

    var squadWins = MutableLiveData<Map<Squad,Int>>()
    var matchesPerSport = MutableLiveData<Map<Sport,Int>>()

    fun loadSquadWins(){
        launchWithLoad({
            squadWins.value = analyticsController.getSquadsMostWins()
        }){}
    }

    fun loadMatchesPerSport(){
        launchWithLoad({
            matchesPerSport.value = analyticsController.getMatchesCountPerSport()
        }){}
    }
}