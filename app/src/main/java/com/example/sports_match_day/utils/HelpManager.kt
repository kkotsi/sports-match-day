package com.example.sports_match_day.utils

import android.animation.ObjectAnimator
import android.view.View
import android.widget.TextView
import androidx.core.animation.doOnEnd

/**
 * Created by Kristo on 29-Mar-21
 * todo: Create help for the apps controls.
 */
@Suppress("UNUSED", "UNUSED_PARAMETER")
object HelpManager {
    fun helpSwipeDelete(helpView: View, helpText: TextView) {
        ObjectAnimator.ofFloat(helpView, "translationX", 0f, 200f).apply {
            duration = 1000
            repeatCount = 3
            start()
        }.doOnEnd {
            ObjectAnimator.ofFloat(helpView, "translationX", 0f).apply {
                duration = 200
                start()
            }
        }
    }

    fun helpSwipeRefresh(helpView: View, helpText: TextView) { }
    fun helpClickSelect(helpView: View, helpText: TextView) { }
    fun helpClickEdit(helpView: View, helpText: TextView) { }
}
