package com.example.sports_match_day.ui.sports.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.MainActivity
import com.example.sports_match_day.ui.OnTouchListener
import com.google.android.material.card.MaterialCardView
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 15-Mar-21
 */
class SportsAddFragment : Fragment() {

    private val viewModel: SportsAddViewModel by viewModel()
    private var nameEditTextView: AutoCompleteTextView? = null
    private var toggleGender: ToggleButton? = null
    private var toggleType: ToggleButton? = null
    private var buttonSave: Button? = null
    private var loader: ProgressBar? = null
    private var genderTextHelp: MaterialCardView? = null
    private var typeTextHelp: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_sport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupGenderToggle()
        setupSaveButton()
        setupNameEditText()
        setupTypeToggle()
        setupHelpButton()
    }

    private fun setupHelpButton(){
        val genderHelp = view?.findViewById<ImageButton>(R.id.button_help_gender)
        genderTextHelp = view?.findViewById<MaterialCardView>(R.id.container_gender_help)
        genderTextHelp?.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                genderTextHelp?.visibility = View.GONE
        }

        genderHelp?.setOnClickListener {
            genderTextHelp?.visibility = View.VISIBLE
            genderTextHelp?.requestFocus()
        }

        val typeHelp = view?.findViewById<ImageButton>(R.id.button_help_type)
        typeTextHelp = view?.findViewById<MaterialCardView>(R.id.container_type_help)
        typeTextHelp?.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                typeTextHelp?.visibility = View.GONE
        }

        typeHelp?.setOnClickListener {
            typeTextHelp?.visibility = View.VISIBLE
            typeTextHelp?.requestFocus()
        }
    }
    private fun setupNameEditText() {
        nameEditTextView = view?.findViewById(R.id.editText_name)
        nameEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = nameEditTextView?.editableText.toString()
                if (country.isBlank()) {
                    nameEditTextView?.error =  getString(R.string.error_blank)
                } else {
                    nameEditTextView?.error = null
                }
            }
        }
    }

    private fun setupSaveButton() {
        buttonSave = view?.findViewById(R.id.button_save)
        buttonSave?.setOnClickListener {
            val gender = toggleGender?.isChecked ?: false
            val type = toggleType?.isChecked ?: false

            val name = nameEditTextView?.text?.toString()?.trim() ?: ""

            if(validateData())
                viewModel.addSport(name, type, gender)
        }
    }

    private fun validateData(): Boolean{
        var pass = true

        val name = nameEditTextView?.text?.toString() ?: ""

        if(nameEditTextView?.error != null){
            pass = false
        }

        if(name.isBlank()){
            nameEditTextView?.error = getString(R.string.error_blank)
            pass = false
        }
        return pass
    }

    private fun setupTypeToggle() {
        toggleType = view?.findViewById(R.id.toggle_gender)
    }

    private fun setupGenderToggle() {
        toggleGender = view?.findViewById(R.id.toggle_gender)
    }

    override fun onResume() {
        super.onResume()

        (requireActivity() as MainActivity).onTouchListener = object: OnTouchListener{
            override fun onTouch() {
                typeTextHelp?.visibility = View.GONE
                genderTextHelp?.visibility = View.GONE
            }
        }
    }
    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).onTouchListener = null
    }
}