package com.example.sports_match_day.ui.athletes.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.athletes.AthletesFragment
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.squads.manage.DateAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 12-Mar-21
 */
class AthletesManageFragment : BaseFragment() {
    private val args: AthletesManageFragmentArgs by navArgs()

    private val viewModel: AthletesManageViewModel by viewModel()
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
        setupForEdit()
    }

    private fun setupForEdit(){
        if(args.athleteId > -1){
            viewModel.loadAthlete(args.athleteId)
        }
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
            val sport = (sportsSpinner.selectedItem as Sport)

            if(validateData()) {
                if (viewModel.athlete.value == null)
                    viewModel.addAthlete(name, city, country, gender, sport.id, birthday)
                else
                    viewModel.updateAthlete(args.athleteId, name, city, country, gender, sport, birthday)
            }
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
            if (isChecked) {
                imagePerson.setImageResource(R.drawable.male)
                viewModel.getSports(Gender.MALE)
            }
            else {
                imagePerson.setImageResource(R.drawable.female)
                viewModel.getSports(Gender.FEMALE)
            }
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

        yearsSpinner.adapter = DateAdapter(requireContext(), R.layout.item_spinner, years)
        monthsSpinner.adapter = DateAdapter(requireContext(), R.layout.item_spinner, months)

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
                    DateAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
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
                    DateAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSportsSpinner(){
        sportsSpinner = requireView().findViewById(R.id.spinner_sport)
        if(args.athleteId < 0)
            viewModel.getSports(Gender.FEMALE)
    }

    private fun setupObservers() {
        loader = requireView().findViewById(R.id.progress_loading)

        viewModel.athlete.observe(viewLifecycleOwner,{

            viewModel.getSports(it.gender)

            nameEditTextView.setText(
                it.name,
                TextView.BufferType.EDITABLE
            )
            citiesEditTextView.setText(
                it.city,
                TextView.BufferType.EDITABLE
            )
            countriesEditTextView.setText(
                it.country.displayCountry,
                TextView.BufferType.EDITABLE
            )

            toggleGender.isChecked = it.gender == Gender.MALE

            if(it.gender == Gender.MALE) {
                imagePerson.setImageResource(R.drawable.male)
            }else{
                imagePerson.setImageResource(R.drawable.female)
            }

            val year = (yearsSpinner.adapter as DateAdapter).getItemPosition(
                "${it.birthday.year}"
            )
            val day =
                (daysSpinner.adapter as DateAdapter).getItemPosition("${it.birthday.dayOfMonth}")

            yearsSpinner.setSelection(year)
            monthsSpinner.setSelection(it.birthday.monthValue - 1)
            daysSpinner.setSelection(day)
        })

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

            viewModel.athlete.value?.let { athlete ->
                sportsSpinner.setSelection(it.indexOf(athlete.sport))
            }
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