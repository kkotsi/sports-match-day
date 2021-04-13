package com.example.sports_match_day.ui.squads

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel
import java.util.*

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsViewModel(private var coreController: CoreController) : ScopedViewModel() {

    var pagedSquads = coreController.getSquads().flow.cachedIn(viewModelScope)
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

    fun removeSquad(squad: Squad?) {

        squad?.let {
            launchWithLoad({
                removeSuccessful.value = coreController.removeSquad(it)
            }) {}
        }
    }

    fun removeSquadAndMatches(squad: Squad?) {
        squad?.let{
            launchWithLoad({
                val success = coreController.removeSquad(squad)
                coreController.removeMatchBySquad(squad)
                removeSuccessful.value = success
            }) {}
        }
    }
}