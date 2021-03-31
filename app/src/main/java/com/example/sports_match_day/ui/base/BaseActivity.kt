package com.example.sports_match_day.ui.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sports_match_day.utils.LocaleManager
import org.koin.core.KoinComponent

/**
 * Created by Kristo on 01-Apr-21
 */
abstract class BaseActivity : AppCompatActivity(), KoinComponent {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //If android version <= 7.1.1 and the language has changed
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            LocaleManager.changeLocaleLegacy(applicationContext, resources)
        }
    }


    override fun attachBaseContext(newBase: Context?) {
        //If android version > 7.1.1 and the language has changed
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            super.attachBaseContext(LocaleManager.changeLocale(newBase))
        }else{
            super.attachBaseContext(newBase)
        }
    }
}