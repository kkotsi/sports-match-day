package com.example.sports_match_day.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
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
}