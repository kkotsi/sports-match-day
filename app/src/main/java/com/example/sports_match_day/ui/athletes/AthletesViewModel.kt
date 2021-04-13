package com.example.sports_match_day.ui.athletes

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.ui.base.ScopedViewModel
import java.util.*

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesViewModel(private var coreController: CoreController) : ScopedViewModel() {

    var pagedAthletes = coreController.getAthletes().flow.cachedIn(viewModelScope)
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

    fun removeAthlete(athlete: Athlete?){
        athlete?.let {
            launchWithLoad({
                removeSuccessful.value = coreController.removeAthlete(it)
            }) {}
        }
    }

    fun removeAthleteAndMatches(athlete: Athlete?) {
        athlete?.let{
            launchWithLoad({
                val success = coreController.removeAthlete(athlete)
                coreController.removeMatchByAthlete(athlete)
                removeSuccessful.value = success
            }) {}
        }
    }
}