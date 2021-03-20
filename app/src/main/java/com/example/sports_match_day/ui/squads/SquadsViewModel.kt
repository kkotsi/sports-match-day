package com.example.sports_match_day.ui.squads

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsViewModel(private var coreController: CoreController) : ScopedViewModel() {

    var pagedSquads = coreController.getSquads().flow.cachedIn(viewModelScope)
    val removeSuccessful = MutableLiveData<Boolean>()

    fun removeSquad(squad: Squad?) {

        squad?.let {
            launchWithLoad({
                removeSuccessful.value = coreController.removeSquad(it)
            }) {}
        }
    }
}