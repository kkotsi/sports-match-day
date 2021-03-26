package com.example.sports_match_day.ui.home.add

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Contestant
import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.ui.base.ScopedViewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 23-Mar-21
 */
class MatchesAddViewModel(private val coreController: CoreController) : ScopedViewModel() {
    val saveSuccessful = MutableLiveData<Boolean>()
    val sports = MutableLiveData<List<Sport>>()
    val contestants = MutableLiveData<MutableList<Contestant>>()

    fun checkCity(context: Context, name: String, checked: (Address?) -> Unit) {
        if (name.isBlank()) return
        Thread {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val locations = geoCoder.getFromLocationName(name, 4)
            locations.forEach {
                if (it.locality != null || it.adminArea != null) {
                    checked.invoke(it)
                    return@Thread
                }
            }
            checked.invoke(null)
        }.run()
    }

    fun getSports() {
        launchWithLoad({
            sports.value = coreController.getAllSports()
        }) { }
    }

    fun addMatch(sport: Sport,city: String, country: String, stadium: String, date: LocalDateTime, participants: List<Participant>) {
        launchWithLoad({
            val countryCode =
                Locale.getISOCountries().find { Locale("", it).displayCountry == country }
                    ?: throw NullPointerException("")
            saveSuccessful.value =
                coreController.addMatch(sport.id, city, countryCode, stadium, date, participants)
        }) {}
    }

    fun getContestants(sport: Sport?){
        sport?.let {
            launchWithLoad({
                val newContestants = mutableListOf<Contestant>()

                if(it.type == SportType.TEAM){
                    newContestants.addAll(coreController.getAllSquads(it.id))
                }else{
                    newContestants.addAll(coreController.getAllAthletes(it.id))
                }
                contestants.value = newContestants
            }) {}
        }
    }
}