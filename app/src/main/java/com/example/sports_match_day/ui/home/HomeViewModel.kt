package com.example.sports_match_day.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.ui.base.ScopedViewModel
import kotlinx.coroutines.flow.catch
import org.threeten.bp.LocalDateTime

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeViewModel(private var coreController: CoreController) : ScopedViewModel() {

    val matches = coreController.getMatches()
        .flow
        .catch {
            _apiErrorMessage.value = it
        }
        .cachedIn(viewModelScope)

    val removeSuccessful = MutableLiveData<Boolean>()

    fun getEventDates(): List<LocalDateTime> {
        return coreController.getMatchEvents()
    }

    fun removeMatch(match: Match?) {
        launchWithLoad({
            match?.let {
                removeSuccessful.value = coreController.removeMatch(match)
            }
        }) {}
    }

    fun addMatch() {
        //Dummy data. Todo: create a screen to get these data.
        launchWithLoad({
            val participants = mutableListOf<Participant>()
            participants.add(Participant(coreController.getSquad(1), 2.0))
            participants.add(Participant(coreController.getSquad(2), 3.0))

            coreController.addMatch("Tirana", "al", 1, LocalDateTime.now(), participants)
        }) {}
    }
}