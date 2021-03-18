package com.example.sports_match_day.ui.home

import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.ui.base.ScopedViewModel
import org.threeten.bp.LocalDateTime

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeViewModel(private var coreController: CoreController) : ScopedViewModel() {

    val matches = coreController.getMatches()
    val removeSuccessful = MutableLiveData<Boolean>()

    fun invalidatedData() {
        matches.value?.dataSource?.invalidate()
    }

    fun getEventDates(): List<LocalDateTime>{
        val eventDates = mutableListOf<LocalDateTime>()
        matches.value?.forEach {
            eventDates.add(it.date)
        }
        return eventDates
    }

    fun removeMatch(match: Match?){
        launchWithLoad({
            match?.let {
                removeSuccessful.value = coreController.removeMatch(match)
            }
        }) {}
    }

    fun addMatch(){
        //Dummy data. Todo: create a screen to get these data.
        launchWithLoad({
            val participants = mutableListOf<Participant>()
            participants.add(Participant(coreController.getSquad(1), 2.0))
            participants.add(Participant(coreController.getSquad(2), 3.0))

            coreController.addMatch("Tirana", "al", 1, LocalDateTime.now(), participants)
        }){}
    }
}