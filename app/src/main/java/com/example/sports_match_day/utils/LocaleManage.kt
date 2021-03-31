package com.example.sports_match_day.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import java.util.*

/**
 * Created by kkotsi on 4/9/2020
 */
object LocaleManager {

    const val GREEK_CODE = "el"
    const val ENGLISH_CODE = "en_US"

    //Add to attachBaseContext of base Activity
    fun changeLocale(newBase: Context?) : Context {
        val newLocale: Locale = if(Prefs.getString(PreferencesKeys.LANGUAGE, ENGLISH_CODE) == GREEK_CODE){
            Locale(GREEK_CODE)
        }else{
            Locale.ENGLISH
        }
        return wrap(newBase!!, newLocale)
    }

    fun setEnglishLanguage(){
        Prefs.putString(PreferencesKeys.LANGUAGE, ENGLISH_CODE)
    }

    fun setGreekLanguage(){
        Prefs.putString(PreferencesKeys.LANGUAGE, GREEK_CODE)
    }

    fun isLanguageEnglish(): Boolean{
        return Prefs.getString(PreferencesKeys.LANGUAGE, ENGLISH_CODE) == ENGLISH_CODE
    }

    //Add to onCreate of base Activity (Android <= 7)
    @Suppress("DEPRECATION")
    fun changeLocaleLegacy(applicationContext: Context, resources : Resources?){
        val activityConf = resources?.configuration
        val newLocale = Locale(Prefs.getString(PreferencesKeys.LANGUAGE, ENGLISH_CODE))
        activityConf?.setLocale(newLocale)
        resources?.updateConfiguration(activityConf, resources.displayMetrics)

        val applicationRes =
            applicationContext.resources
        val applicationConf =
            applicationRes.configuration
        applicationConf.setLocale(newLocale)
        applicationRes.updateConfiguration(
            applicationConf,
            applicationRes.displayMetrics
        )
    }

    @Suppress("NAME_SHADOWING")
    fun wrap(context: Context, newLocale: Locale): ContextWrapper {
        var context = context
        val res = context.resources
        val configuration = res.configuration

        Locale.setDefault(newLocale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale)

            val localeList = LocaleList(newLocale)
            LocaleList.setDefault(localeList)
            configuration.setLocales(localeList)
            context = context.createConfigurationContext(configuration)
            context.resources.configuration.setLocale(newLocale)

        } else {
            configuration.setLocale(newLocale)
            context = context.createConfigurationContext(configuration)
            context.resources.configuration.setLocale(newLocale)
        }

        return ContextWrapper(context)
    }
}