package com.example.sports_match_day.ui

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.controllers.LoginController
import com.example.sports_match_day.ui.base.ScopedViewModel

/**
 * Created by Kristo on 16-Mar-21
 */
class LoginViewModel(
    private var loginController: LoginController,
    private var coreController: CoreController
) : ScopedViewModel() {

    val samplesDataLoaded = MutableLiveData<Boolean>()
    val signUp = MutableLiveData<Boolean>()

    fun signUpUser() {
        launchWithLoad({
            signUp.value = loginController.signUp()
        }) {}
    }

    fun loadData() {
        launchWithLoad({
            samplesDataLoaded.value = coreController.loadSamples()
        }) {}
    }
}