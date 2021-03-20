package com.example.sports_match_day.ui.athletes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesViewModel(private var coreController: CoreController) : ScopedViewModel() {

    var pagedAthletes = coreController.getAthletes().flow.cachedIn(viewModelScope)
    val removeSuccessful = MutableLiveData<Boolean>()

    fun removeAthlete(athlete: Athlete?){
        athlete?.let {
            launchWithLoad({
                removeSuccessful.value = coreController.removeAthlete(it)
            }) {}
        }
    }
}