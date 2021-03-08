package com.example.sports_match_day.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
import org.threeten.bp.LocalDateTime

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeViewModel(private var coreController: CoreController) : ViewModel() {

    val matches = MutableLiveData<List<Match>>()

    fun loadMatches(){
        coreController.loadMatches(matches)
    }

    fun getEventDates(): List<LocalDateTime>{
        val eventDates = mutableListOf<LocalDateTime>()
        matches.value?.forEach {
            eventDates.add(it.date)
        }
        return eventDates
    }
}