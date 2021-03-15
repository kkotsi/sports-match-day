package com.example.sports_match_day.ui.sports.add

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 15-Mar-21
 */
class SportsAddViewModel(private val coreController: CoreController) : ScopedViewModel() {
    val saveSuccessful = MutableLiveData<Boolean>()

    fun addSport(
        name: String,
        type: Boolean,
        gender: Boolean
    ) {
        launchWithLoad({
            saveSuccessful.value =
                coreController.addSport(name, type, gender)
        }) {}
    }
}