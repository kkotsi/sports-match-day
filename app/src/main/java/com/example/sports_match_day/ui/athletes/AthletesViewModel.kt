package com.example.sports_match_day.ui.athletes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesViewModel(private var coreController: CoreController) : ScopedViewModel() {
    val squads = MutableLiveData<List<Squad>>()

    var pagedAthletes: LiveData<PagedList<Athlete>> = coreController.getAthletes()

    fun removeAthlete(athlete: Athlete?){
        athlete?.let {
            launchWithLoad({
                coreController.removeAthlete(it)
            }){

            }
        }
    }
}