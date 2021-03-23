package com.example.sports_match_day.ui.squads.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.athletes.add.SportsAdapter
import com.example.sports_match_day.ui.squads.SquadsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 14-Mar-21
 */
class SquadsAddFragment: Fragment() {

    private val viewModel: SquadsAddViewModel by viewModel()
    private var citiesEditTextView: AutoCompleteTextView? = null
    private var countriesEditTextView: AutoCompleteTextView? = null
    private var nameEditTextView: AutoCompleteTextView? = null
    private var stadiumEditTextView: AutoCompleteTextView? = null
    private var nameTextView: TextView? = null
    private var buttonSave: Button? = null
    private var loader: ProgressBar? = null

    private var daysSpinner: Spinner? = null
    private var monthsSpinner: Spinner? = null
    private var yearsSpinner: Spinner? = null
    private var sportsSpinner: Spinner? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_squad, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupBirthday()
        setupEditTextCountry()
        setupEditTextCity()
        setupSaveButton()
        setupNameEditText()
        setupStadiumEditText()
        setupSportsSpinner()
    }

    private fun setupStadiumEditText(){
        stadiumEditTextView = view?.findViewById(R.id.editText_name)
        stadiumEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val stadium = stadiumEditTextView?.editableText.toString()
                if (stadium.isBlank()) {
                    stadiumEditTextView?.error =  getString(R.string.error_blank)
                } else {
                    stadiumEditTextView?.error = null
                }
            }
        }
    }

    private fun setupNameEditText() {
        nameTextView = view?.findViewById(R.id.text_name)
        nameEditTextView = view?.findViewById(R.id.editText_name)
        nameEditTextView?.addTextChangedListener {
            nameTextView?.text = it?.toString()
        }
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
            val birthday = LocalDateTime.of(
                yearsSpinner?.selectedItem?.toString()?.toInt() ?: 0,
                (monthsSpinner?.selectedItemPosition ?: 0) + 1,
                daysSpinner?.selectedItem?.toString()?.toInt() ?: 1,
                1,
                1
            )

            val city = citiesEditTextView?.text?.toString()?.trim() ?: ""
            val country = countriesEditTextView?.text?.toString()?.trim() ?: ""
            val name = nameEditTextView?.text?.toString()?.trim() ?: ""
            val stadium = stadiumEditTextView?.text?.toString()?.trim() ?: ""
            val sportId = sportsSpinner?.selectedItem as Int

            if(validateData())
                viewModel.addSquad(name, city, country, stadium, sportId, birthday)
        }
    }

    private fun validateData(): Boolean{
        var pass = true

        val city = citiesEditTextView?.text?.toString() ?: ""
        val country = countriesEditTextView?.text?.toString() ?: ""
        val name = nameEditTextView?.text?.toString() ?: ""
        val stadium = stadiumEditTextView?.text?.toString() ?: ""

        if(citiesEditTextView?.error != null){
            pass = false
        }
        if(countriesEditTextView?.error != null){
            pass = false
        }
        if(nameEditTextView?.error != null){
            pass = false
        }
        if(stadiumEditTextView?.error != null){
            pass = false
        }

        if(stadium.isBlank()){
            stadiumEditTextView?.error = getString(R.string.error_blank)
            pass = false
        }
        if(city.isBlank()){
            citiesEditTextView?.error = getString(R.string.error_blank)
            pass = false
        }
        if(country.isBlank()){
            countriesEditTextView?.error = getString(R.string.error_blank)
            pass = false
        }
        if(name.isBlank()){
            nameEditTextView?.error = getString(R.string.error_blank)
            pass = false
        }
        return pass
    }

    private fun setupBirthday() {
        daysSpinner = view?.findViewById(R.id.spinner_day)
        monthsSpinner = view?.findViewById(R.id.spinner_month)
        yearsSpinner = view?.findViewById(R.id.spinner_year)

        val yearNow = LocalDateTime.now().year

        val years = mutableListOf<String>()
        val months = resources.getString(R.string.months).split(",")

        repeat(100) {
            years.add("${yearNow - it}")
        }

        yearsSpinner?.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, years)
        monthsSpinner?.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, months)

        yearsSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    yearsSpinner?.selectedItem?.toString()?.toInt() ?: 0,
                    (monthsSpinner?.selectedItemPosition ?: 0) + 1,
                    1,
                    1,
                    1
                )
                daysSpinner?.adapter =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        monthsSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    yearsSpinner?.selectedItem?.toString()?.toInt() ?: 0,
                    position + 1,
                    1,
                    1,
                    1
                )
                daysSpinner?.adapter =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSportsSpinner(){
        sportsSpinner = view?.findViewById(R.id.spinner_sport)
        viewModel.getSports()
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
                //This will trigger the SquadsFragment to refresh.
                navController.previousBackStackEntry?.savedStateHandle?.set(SquadsFragment.SQUADS_REFRESH_KEY, true)
                navController.navigateUp()
            }
        })
        viewModel.sports.observe(viewLifecycleOwner, {
            sportsSpinner?.adapter = SportsAdapter(requireContext(), it)
        })
    }

    private fun setupEditTextCity() {
        citiesEditTextView = view?.findViewById(R.id.editText_city)

        citiesEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = citiesEditTextView?.editableText.toString()
                viewModel.checkCity(requireContext(), country) {
                    if (it == null) {
                        citiesEditTextView?.error = getString(R.string.error_empty_city)
                    } else {
                        citiesEditTextView?.error = null
                        citiesEditTextView?.setText(
                            it.locality ?: it.adminArea,
                            TextView.BufferType.EDITABLE
                        )
                    }
                }
            }
        }
    }

    private fun setupEditTextCountry() {
        countriesEditTextView = view?.findViewById(R.id.editText_country)

        val countries = mutableListOf<String>()
        Locale.getAvailableLocales().forEach {
            if(!countries.contains(it.displayCountry)){
                countries.add(it.displayCountry)
            }
        }

        countriesEditTextView?.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = countriesEditTextView?.editableText.toString()
                if (!countries.contains(country)) {
                    countriesEditTextView?.error = getString(R.string.error_empty_country)
                } else {
                    countriesEditTextView?.error = null
                }
            }
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            countries
        ).also { adapter ->
            countriesEditTextView?.setAdapter(adapter)
        }
    }
}