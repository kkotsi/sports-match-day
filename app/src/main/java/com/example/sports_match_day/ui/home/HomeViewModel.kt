package com.example.sports_match_day.ui.home

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.ui.base.ScopedViewModel
import kotlinx.coroutines.flow.catch
import org.threeten.bp.LocalDateTime
import java.util.*

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

    fun getCity(context: Context, name: String, checked: (Address?) -> Unit) {
        if (name.isBlank()) return
        Thread {
            _isDataLoading.value = true
            val geoCoder = Geocoder(context, Locale.getDefault())
            val locations = geoCoder.getFromLocationName(name, 4)
            _isDataLoading.value = false
            locations.forEach {
                if (it.locality != null || it.adminArea != null) {
                    checked.invoke(it)
                    return@Thread
                }
            }
            checked.invoke(null)
        }.run()
    }

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