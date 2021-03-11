package com.example.sports_match_day.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel
import org.threeten.bp.LocalDateTime
import timber.log.Timber

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeViewModel(private var coreController: CoreController) : ScopedViewModel() {

    val matches = MutableLiveData<List<Match>>()
    val squads = MutableLiveData<List<Squad>>()

    fun loadData(){
        launchWithLoad({
            coreController.loadSamples()
            coreController.loadMatches(matches)
        })
        {
            Timber.e("Couldn't load the matches: $it" )
        }
    }

    fun getEventDates(): List<LocalDateTime>{
        val eventDates = mutableListOf<LocalDateTime>()
        matches.value?.forEach {
            eventDates.add(it.date)
        }
        return eventDates
    }
}