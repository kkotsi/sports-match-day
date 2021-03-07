package com.example.sports_match_day.room

import androidx.room.TypeConverter
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Squad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by Kristo on 07-Mar-21
 */
class ListConverter {

    @TypeConverter
    fun fromSquadList(countryLang: List<Squad>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Squad?>?>() {}.type
        return gson.toJson(countryLang, type)
    }

    @TypeConverter
    fun fromAthleteList(countryLang: List<Athlete>?): String? {
        if (countryLang == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Athlete?>?>() {}.type
        return gson.toJson(countryLang, type)
    }
}
