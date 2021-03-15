package com.example.sports_match_day.ui.squads

import androidx.lifecycle.LiveData
import androidx.paging.PagedList
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsViewModel(private var coreController: CoreController) : ScopedViewModel() {

    var pagedSquads: LiveData<PagedList<Squad>> = coreController.getSquads()

    fun invalidatedData(){
        pagedSquads.value?.dataSource?.invalidate()
    }

    fun removeSquad(squad: Squad?){
        squad?.let {
            launchWithLoad({
                coreController.removeSquad(it)
            }){

            }
        }
    }
}