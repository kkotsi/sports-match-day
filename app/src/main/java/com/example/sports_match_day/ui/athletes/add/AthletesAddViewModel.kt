package com.example.sports_match_day.ui.athletes.add

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.MutableLiveData
import com.example.sports_match_day.controllers.CoreController
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.base.ScopedViewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 12-Mar-21
 */
class AthletesAddViewModel(private val coreController: CoreController) : ScopedViewModel() {
    val saveSuccessful = MutableLiveData<Boolean>()
    val sports = MutableLiveData<List<Sport>>()

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

    fun getDays(year: LocalDateTime): List<String> {
        val numberOfDays = year.month.length(year.toLocalDate().isLeapYear)
        val days = mutableListOf<String>()
        repeat(numberOfDays) {
            days.add("${it + 1}")
        }
        return days
    }

    fun getSports() {
        launchWithLoad({
            sports.value = coreController.getAllSports()
        }) { }
    }

    fun addAthlete(
        name: String,
        city: String,
        country: String,
        gender: Boolean,
        birthday: LocalDateTime
    ) {
        launchWithLoad({
            val countryCode =
                Locale.getISOCountries().find { Locale("", it).displayCountry == country }
                    ?: throw NullPointerException("")
            saveSuccessful.value =
                coreController.addAthlete(name, city, countryCode, gender, birthday)
        }) {
            print(it)
        }
    }
}