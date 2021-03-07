package com.example.sports_match_day.room

import androidx.room.TypeConverter
import com.example.sports_match_day.room.model.Athlete
import com.example.sports_match_day.room.model.Sport
import com.example.sports_match_day.room.model.Squad
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by Kristo on 07-Mar-21
 */
class ListConverter {

    @TypeConverter
    fun fromSquadList(squads: List<Squad>?): String? {
        if (squads == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Squad?>?>() {}.type
        return gson.toJson(squads, type)
    }

    @TypeConverter
    fun fromAthleteList(athletes: List<Athlete>?): String? {
        if (athletes == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Athlete?>?>() {}.type
        return gson.toJson(athletes, type)
    }

    @TypeConverter
    fun fromSportList(sports: List<Sport>?): String? {
        if (sports == null) {
            return null
        }
        val gson = Gson()
        val type: Type = object : TypeToken<List<Athlete?>?>() {}.type
        return gson.toJson(sports, type)
    }
}
