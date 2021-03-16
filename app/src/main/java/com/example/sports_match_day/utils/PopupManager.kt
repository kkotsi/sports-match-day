package com.example.sports_match_day.utils

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.sports_match_day.R

/**
 * Created by Kristo on 16-Mar-21
 */
object PopupManager {

    fun simplePopupMessage(context: Context, message: String): AlertDialog {
        val mBuilder = AlertDialog.Builder(context)
        val mView = View.inflate(context, R.layout.pop_up, null)
        val alertDialog = mBuilder.setView(mView).create()
        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.colorPrimary))

        mView.findViewById<TextView>(R.id.text_pop_up_description).text = message
        mView.findViewById<Button>(R.id.button_pop_up_yes).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return alertDialog
    }
}