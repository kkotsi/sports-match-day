package com.example.sports_match_day.ui

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 16-Mar-21
 */
class LoginViewModel(private var coreController: CoreController) : ScopedViewModel() {

    val samplesDataLoaded = MutableLiveData<Boolean>()

    fun loadData(){
        launchWithLoad({
            samplesDataLoaded.value = coreController.loadSamples()
        }) {}
    }
}