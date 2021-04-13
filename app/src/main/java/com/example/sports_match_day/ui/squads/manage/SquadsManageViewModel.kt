package com.example.sports_match_day.ui.squads.manage

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.ui.base.ScopedViewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 14-Mar-21
 */
class SquadsManageViewModel(private val coreController: CoreController) : ScopedViewModel() {
    val saveSuccessful = MutableLiveData<Boolean>()
    val sports = MutableLiveData<List<Sport>>()
    val squad = MutableLiveData<Squad>()

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

    fun getSports(gender: Gender) {
        launchWithLoad({
            sports.value = coreController.getAllSports(SportType.TEAM, gender)
        }) { }
    }

    fun getDays(year: LocalDateTime): List<String> {
        val numberOfDays = year.month.length(year.toLocalDate().isLeapYear)
        val days = mutableListOf<String>()
        repeat(numberOfDays) {
            days.add("${it + 1}")
        }
        return days
    }

    fun addSquad(
        name: String,
        city: String,
        country: String,
        stadium: String,
        sportId: Int,
        birthday: LocalDateTime,
        gender: Boolean
    ) {
        launchWithLoad({
            val countryCode =
                Locale.getISOCountries().find { Locale("", it).displayCountry == country }
                    ?: throw NullPointerException("")
            saveSuccessful.value =
                coreController.addSquad(name, city, countryCode, stadium, sportId, birthday, gender)
        }) {
            print(it)
        }
    }

    fun loadSquad(squadId: Int) {
        launchWithLoad({
            squad.value = coreController.getSquad(squadId)
        }) { }
    }

    fun updateSquad(
        id: Int,
        name: String,
        city: String,
        country: String,
        stadium: String,
        sport: Sport,
        birthday: LocalDateTime,
        gender: Boolean
    ) {
        launchWithLoad({

            val countryCode =
                Locale.getISOCountries().find { Locale("", it).displayCountry == country }
                    ?: throw NullPointerException("")
            saveSuccessful.value =
                coreController.updateSquad(id,name, city, countryCode, stadium, sport, birthday, gender, squad.value?.matches ?: mutableListOf())
        }) {}
    }
}