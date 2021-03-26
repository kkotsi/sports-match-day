package com.example.sports_match_day.ui.sports.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.MainActivity
import com.example.sports_match_day.ui.OnTouchListener
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.sports.SportsFragment
import com.google.android.material.card.MaterialCardView
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 15-Mar-21
 */
class SportsAddFragment : BaseFragment() {

    private val viewModel: SportsAddViewModel by viewModel()
    private var nameEditTextView: AutoCompleteTextView? = null
    private var participantsCountEditTextView: AutoCompleteTextView? = null
    private var toggleGender: ToggleButton? = null
    private var toggleType: ToggleButton? = null
    private var buttonSave: Button? = null
    private var loader: ProgressBar? = null
    private var genderTextHelp: MaterialCardView? = null
    private var typeTextHelp: MaterialCardView? = null
    private var participantTextHelp: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_sport, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupGenderToggle()
        setupSaveButton()
        setupNameEditText()
        setupParticipantCountEditText()
        setupTypeToggle()
        setupHelpButton()
    }

    private fun setupObservers() {
        loader = view?.findViewById(R.id.progress_loading)
        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            if (it)
                loader?.visibility = View.VISIBLE
            else
                loader?.visibility = View.INVISIBLE
        })

        viewModel.saveSuccessful.observe(viewLifecycleOwner, {
            if(it) {
                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                //This will trigger the SportsFragment to refresh.
                navController.previousBackStackEntry?.savedStateHandle?.set(SportsFragment.SPORTS_REFRESH_KEY, true)
                navController.navigateUp()
            }
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })
    }

    private fun setupParticipantCountEditText(){
        participantsCountEditTextView = view?.findViewById(R.id.editText_participants_count)
        participantsCountEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = participantsCountEditTextView?.editableText.toString()
                if (country.isBlank()) {
                    participantsCountEditTextView?.error =  getString(R.string.error_blank)
                } else {
                    participantsCountEditTextView?.error = null
                }
            }
        }
    }

    private fun setupHelpButton(){
        //Gender
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

        //Type
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

        //Count
        val participantHelp = view?.findViewById<ImageButton>(R.id.button_help_count)
        participantTextHelp = view?.findViewById<MaterialCardView>(R.id.container_participants_help)
        participantTextHelp?.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus)
                participantTextHelp?.visibility = View.GONE
        }

        participantHelp?.setOnClickListener {
            participantTextHelp?.visibility = View.VISIBLE
            participantTextHelp?.requestFocus()
        }
    }
    private fun setupNameEditText() {
        nameEditTextView = view?.findViewById(R.id.editText_name)
        nameEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val name = nameEditTextView?.editableText.toString()
                if (name.isBlank()) {
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

            val count = participantsCountEditTextView?.text?.toString()?.trim() ?: "0"
            val name = nameEditTextView?.text?.toString()?.trim() ?: ""

            if(validateData())
                viewModel.addSport(name, type, gender, count.toInt())
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

        val count = participantsCountEditTextView?.text?.toString() ?: ""

        if(participantsCountEditTextView?.error != null){
            pass = false
        }

        if(count.isBlank()){
            participantsCountEditTextView?.error = getString(R.string.error_blank)
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
                participantTextHelp?.visibility = View.GONE
            }
        }
    }
    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).onTouchListener = null
    }
}