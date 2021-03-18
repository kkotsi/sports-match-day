package com.example.sports_match_day.ui.sports

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsViewModel(private val coreController: CoreController) : ScopedViewModel() {
    var pagedSports: LiveData<PagedList<Sport>> = coreController.getSports()
    val removeSuccessful = MutableLiveData<Boolean>()

    fun invalidatedData() {
        pagedSports.value?.dataSource?.invalidate()
    }

    fun removeSport(sport: Sport?) {
        sport?.let {
            launchWithLoad({
                    removeSuccessful.value = coreController.removeSport(it)
            }) {}
        }
    }
}