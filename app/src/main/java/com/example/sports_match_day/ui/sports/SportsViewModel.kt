package com.example.sports_match_day.ui.sports

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsViewModel(private val coreController: CoreController) : ScopedViewModel() {

    var pagedSports = coreController.getSports().flow.cachedIn(viewModelScope)
    val removeSuccessful = MutableLiveData<Boolean>()

    fun removeSport(sport: Sport?) {
        sport?.let {
            launchWithLoad({
                removeSuccessful.value = coreController.removeSport(it)
            }) {}
        }
    }

    fun removeSportAndMatches(sport: Sport?) {
        sport?.let {
            launchWithLoad({
                val success = coreController.removeSport(it)
                coreController.removeMatchBySport(it)
                removeSuccessful.value = success
            }) {}
        }
    }

    fun removeAthletesAndSquadsAndMatches(sport: Sport?){
        sport?.let {
            launchWithLoad({
                val success = coreController.removeSport(it)
                coreController.removeSquadBySport(it)
                coreController.removeAthleteBySport(it)
                coreController.removeMatchBySport(it)
                removeSuccessful.value = success
            }) {}
        }
    }
}