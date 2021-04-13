package com.example.sports_match_day.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * Created by Kristo on 07-Mar-21
 */
class ListConverter {

    @TypeConverter
    fun fromMatches(matchIds: List<Int>?): String? {
        if(matchIds == null)
            return null
        val gson = Gson()
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.toJson(matchIds, type)
    }

    @TypeConverter
    fun toMatches(matchIds: String?): MutableList<Int> {
        if(matchIds == null || matchIds.isBlank())
            return mutableListOf()
        val gson = Gson()
        val type: Type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(matchIds, type)
    }
}
