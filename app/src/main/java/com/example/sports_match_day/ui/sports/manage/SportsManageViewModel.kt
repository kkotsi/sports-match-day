package com.example.sports_match_day.ui.sports.manage

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 15-Mar-21
 */
class SportsManageViewModel(private val coreController: CoreController) : ScopedViewModel() {
    val saveSuccessful = MutableLiveData<Boolean>()
    val sport = MutableLiveData<Sport>()

    fun loadSport(sportId: Int) {
        launchWithLoad({
            sport.value = coreController.getSport(sportId)
        }) { }
    }

    fun updateSport(
        id: Int,
        name: String,
        type: Boolean,
        gender: Boolean,
        count: Int
    ) {
        launchWithLoad({
            saveSuccessful.value =
                coreController.updateSport(id, name, type, gender, count)
        }) {}
    }

    fun addSport(
        name: String,
        type: Boolean,
        gender: Boolean,
        count: Int
    ) {
        launchWithLoad({
            saveSuccessful.value =
                coreController.addSport(name, type, gender, count)
        }) {}
    }
}