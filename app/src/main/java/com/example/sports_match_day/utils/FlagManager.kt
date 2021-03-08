package com.example.sports_match_day.utils

/**
 * Created by Kristo on 07-Mar-21
 */
class FlagManager {

    companion object{
        private const val baseUrl = "https://www.countryflags.io/"
        private const val flag = "/flat/64.png"

        fun getFlagURL(countryCode: String): String{
            return baseUrl + countryCode + flag
        }
    }
}