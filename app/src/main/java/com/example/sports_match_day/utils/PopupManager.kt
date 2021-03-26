package com.example.sports_match_day.utils

import android.content.Context
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.sports_match_day.R
import org.threeten.bp.LocalDateTime

/**
 * Created by Kristo on 16-Mar-21
 */
object PopupManager {

    fun simplePopupMessage(context: Context, message: String): AlertDialog {
        val mBuilder = AlertDialog.Builder(context)
        val mView = View.inflate(context, R.layout.pop_up, null)
        val alertDialog = mBuilder.setView(mView).create()
//        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.colorPrimary))

        mView.findViewById<TextView>(R.id.text_pop_up_description).text = message
        mView.findViewById<Button>(R.id.button_pop_up_yes).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return alertDialog
    }

    fun datePickerPopup(context: Context, onDateSelect: (LocalDateTime) -> Unit): AlertDialog {
        val mBuilder = AlertDialog.Builder(context)
        val mView = View.inflate(context, R.layout.pop_up_date_picker, null)
        val alertDialog = mBuilder.setView(mView).create()
        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.colorTransparent))

        val datePicker = mView.findViewById<DatePicker>(R.id.datePicker_popup)

        mView.findViewById<Button>(R.id.button_ok).setOnClickListener {
            alertDialog.dismiss()
            val selectedDate = LocalDateTime.of(datePicker.year,datePicker.month + 1,datePicker.dayOfMonth,0,0)
            onDateSelect.invoke(selectedDate)
        }

        mView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return alertDialog
    }

    @Suppress("DEPRECATION")
    fun timePickerPopup(context: Context, onDateSelect: (Int, Int) -> Unit): AlertDialog {
        val mBuilder = AlertDialog.Builder(context)
        val mView = View.inflate(context, R.layout.pop_up_time_picker, null)
        val alertDialog = mBuilder.setView(mView).create()

        val timePicker = mView.findViewById<TimePicker>(R.id.timePicker_popup)

        mView.findViewById<Button>(R.id.button_ok).setOnClickListener {
            alertDialog.dismiss()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                onDateSelect.invoke(timePicker.hour,timePicker.minute)
            }else{
                onDateSelect.invoke(timePicker.currentHour,timePicker.currentMinute)
            }
        }

        mView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return alertDialog
    }

    fun contestantPickerPopup(context: Context, hint: String, contestants: List<String>, onContestantSelected: (String) -> Unit): AlertDialog {
        val mBuilder = AlertDialog.Builder(context)
        val mView = View.inflate(context, R.layout.pop_up_contestant_picker, null)
        val alertDialog = mBuilder.setView(mView).create()
        alertDialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context,R.color.colorTransparent))

        val contestantsEditTextView =  mView.findViewById<AutoCompleteTextView>(R.id.editText_contestant)
        contestantsEditTextView.hint = hint
        contestantsEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = contestantsEditTextView.editableText.toString()
                if (!contestants.contains(country)) {
                    contestantsEditTextView.error = context.getString(R.string.error_empty_country)
                } else {
                    contestantsEditTextView.error = null
                }
            }
        }

        ArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            contestants
        ).also { adapter ->
            contestantsEditTextView.setAdapter(adapter)
        }

        mView.findViewById<Button>(R.id.button_ok).setOnClickListener {
            alertDialog.dismiss()
            val contestant = contestantsEditTextView.text.toString().trim()
            if(contestants.contains(contestant))
                onContestantSelected.invoke(contestant)
        }

        mView.findViewById<Button>(R.id.button_cancel).setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
        return alertDialog
    }
}