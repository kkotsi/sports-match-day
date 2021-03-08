package com.example.sports_match_day.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeViewModel(private var coreController: CoreController) : ViewModel() {

    val matches = MutableLiveData<List<Match>>()

    fun loadMatches(){
        coreController.loadMatches(matches)
    }
}