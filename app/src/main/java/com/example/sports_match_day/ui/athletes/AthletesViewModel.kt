package com.example.sports_match_day.ui.athletes

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesViewModel(private var coreController: CoreController) : ScopedViewModel() {
    var pagedAthletes: LiveData<PagedList<Athlete>> = coreController.getAthletes()

    fun invalidatedData(){
        pagedAthletes.value?.dataSource?.invalidate()
    }

    fun removeAthlete(athlete: Athlete?){
        athlete?.let {
            launchWithLoad({
                coreController.removeAthlete(it)
            }){

            }
        }
    }
}