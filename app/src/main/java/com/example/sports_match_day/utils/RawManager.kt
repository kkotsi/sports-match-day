package com.example.sports_match_day.utils

import android.content.Context
import com.example.sports_match_day.R
import com.example.sports_match_day.model.network.Match
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.*


/**
 * Created by Kristo on 08-Mar-21
 * This class is responsible to load the matches data from the raw folder.
 * These data will be only used for testing.
 */
class RawManager {
    companion object{
        fun getMatchesRaw(context: Context): List<Match>{
            val input: InputStream = context.resources.openRawResource(R.raw.matches)
            val writer: Writer = StringWriter()
            val buffer = CharArray(1024)

            input.use {
                val reader: Reader = BufferedReader(InputStreamReader(input, "UTF-8"))
                var n: Int
                while (reader.read(buffer).also { n = it } != -1) {
                    writer.write(buffer, 0, n)
                }
            }

            val json = writer.toString()

            val myType = object : TypeToken<List<Match>>() {}.type
            return Gson().fromJson(json, myType)
        }
    }
}