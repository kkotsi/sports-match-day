package com.example.sports_match_day.ui.athletes.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.athletes.AthletesFragment
import com.example.sports_match_day.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 12-Mar-21
 */
class AthletesAddFragment : BaseFragment() {

    private val viewModel: AthletesAddViewModel by viewModel()
    private lateinit var citiesEditTextView: AutoCompleteTextView
    private lateinit var countriesEditTextView: AutoCompleteTextView
    private lateinit var nameEditTextView: AutoCompleteTextView
    private lateinit var toggleGender: ToggleButton
    private lateinit var imagePerson: ImageView
    private lateinit var buttonSave: Button
    private lateinit var loader: ProgressBar

    private lateinit var daysSpinner: Spinner
    private lateinit var monthsSpinner: Spinner
    private lateinit var yearsSpinner: Spinner
    private lateinit var sportsSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_athlete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupGenderToggle()
        setupBirthday()
        setupEditTextCountry()
        setupEditTextCity()
        setupSaveButton()
        setupNameEditText()
        setupSportsSpinner()
    }

    private fun setupNameEditText() {
        view?.findViewById<AutoCompleteTextView>(R.id.editText_name)?.let{
            nameEditTextView = it
        }

        nameEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = nameEditTextView.editableText.toString()
                if (country.isBlank()) {
                    nameEditTextView.error =  getString(R.string.error_blank)
                } else {
                    nameEditTextView.error = null
                }
            }
        }
    }

    private fun setupSaveButton() {
        buttonSave = requireView().findViewById(R.id.button_save)
        buttonSave.setOnClickListener {
            val gender = toggleGender.isChecked
            val birthday = LocalDateTime.of(
                yearsSpinner.selectedItem?.toString()?.toInt() ?: 0,
                monthsSpinner.selectedItemPosition + 1,
                daysSpinner.selectedItem?.toString()?.toInt() ?: 1,
                1,
                1
            )

            val city = citiesEditTextView.text.toString().trim()
            val country = countriesEditTextView.text.toString().trim()
            val name = nameEditTextView.text.toString().trim()
            val sportId = (sportsSpinner.selectedItem as Sport).id

            if(validateData())
                viewModel.addAthlete(name, city, country, gender, sportId, birthday)
        }
    }

    private fun validateData(): Boolean{
        var pass = true

        val city = citiesEditTextView.text.toString()
        val country = countriesEditTextView.text.toString()
        val name = nameEditTextView.text.toString()

        if(citiesEditTextView.error != null){
            pass = false
        }
        if(countriesEditTextView.error != null){
            pass = false
        }
        if(nameEditTextView.error != null){
            pass = false
        }

        if(city.isBlank()){
            citiesEditTextView.error = getString(R.string.error_blank)
            pass = false
        }
        if(country.isBlank()){
            countriesEditTextView.error = getString(R.string.error_blank)
            pass = false
        }
        if(name.isBlank()){
            nameEditTextView.error = getString(R.string.error_blank)
            pass = false
        }
        return pass
    }

    private fun setupGenderToggle() {
        toggleGender = requireView().findViewById(R.id.toggle_gender)
        imagePerson = requireView().findViewById(R.id.image_person)
        toggleGender.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                imagePerson.setImageResource(R.drawable.male)
            else
                imagePerson.setImageResource(R.drawable.female)
        }
    }

    private fun setupBirthday() {
        daysSpinner = requireView().findViewById(R.id.spinner_day)
        monthsSpinner = requireView().findViewById(R.id.spinner_month)
        yearsSpinner = requireView().findViewById(R.id.spinner_year)

        val yearNow = LocalDateTime.now().year

        val years = mutableListOf<String>()
        val months = resources.getString(R.string.months).split(",")

        repeat(100) {
            years.add("${yearNow - it}")
        }

        yearsSpinner.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, years)
        monthsSpinner.adapter = ArrayAdapter(requireContext(), R.layout.item_spinner, months)

        yearsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    yearsSpinner.selectedItem?.toString()?.toInt() ?: 0,
                    monthsSpinner.selectedItemPosition + 1,
                    1,
                    1,
                    1
                )
                daysSpinner.adapter =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        monthsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    yearsSpinner.selectedItem?.toString()?.toInt() ?: 0,
                    position + 1,
                    1,
                    1,
                    1
                )
                daysSpinner.adapter =
                    ArrayAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSportsSpinner(){
        sportsSpinner = requireView().findViewById(R.id.spinner_sport)
        viewModel.getSports()
    }

    private fun setupObservers() {
        loader = requireView().findViewById(R.id.progress_loading)
        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            if (it)
                loader.visibility = View.VISIBLE
            else
                loader.visibility = View.INVISIBLE
        })

        viewModel.saveSuccessful.observe(viewLifecycleOwner, {
            if(it) {
                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

                //This will trigger the AthletesFragment to refresh.
                navController.previousBackStackEntry?.savedStateHandle?.set(AthletesFragment.ATHLETES_REFRESH_KEY, true)
                navController.navigateUp()
            }
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.sports.observe(viewLifecycleOwner, {
            sportsSpinner.adapter = SportsAdapter(requireContext(), it)
        })
    }

    private fun setupEditTextCity() {
        citiesEditTextView = requireView().findViewById(R.id.editText_city)

        citiesEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = citiesEditTextView.editableText.toString()
                viewModel.checkCity(requireContext(), country) {
                    if (it == null) {
                        citiesEditTextView.error = getString(R.string.error_empty_city)
                    } else {
                        citiesEditTextView.error = null
                        citiesEditTextView.setText(
                            it.locality ?: it.adminArea,
                            TextView.BufferType.EDITABLE
                        )
                    }
                }
            }
        }
    }

    private fun setupEditTextCountry() {
        countriesEditTextView = requireView().findViewById(R.id.editText_country)

        val countries = mutableListOf<String>()
        Locale.getAvailableLocales().forEach {
            if(!countries.contains(it.displayCountry)){
                countries.add(it.displayCountry)
            }
        }

        countriesEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = countriesEditTextView.editableText.toString()
                if (!countries.contains(country)) {
                    countriesEditTextView.error = getString(R.string.error_empty_country)
                } else {
                    countriesEditTextView.error = null
                }
            }
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            countries
        ).also { adapter ->
            countriesEditTextView.setAdapter(adapter)
        }
    }
}