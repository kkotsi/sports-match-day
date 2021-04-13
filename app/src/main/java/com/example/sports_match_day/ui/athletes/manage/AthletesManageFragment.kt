package com.example.sports_match_day.ui.athletes.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentAddAthleteBinding
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.athletes.AthletesFragment
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.base.DateAdapter
import com.example.sports_match_day.ui.base.SportsAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 12-Mar-21
 */
class AthletesManageFragment : BaseFragment() {
    private val args: AthletesManageFragmentArgs by navArgs()

    private val viewModel: AthletesManageViewModel by viewModel()
    private var _binding: FragmentAddAthleteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAthleteBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private fun setupForEdit() {
        if (args.athleteId > -1) {
            viewModel.loadAthlete(args.athleteId)
        }
    }

    private fun setupNameEditText() {
        binding.editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = binding.editTextName.editableText.toString()
                if (country.isBlank()) {
                    binding.editTextName.error = getString(R.string.error_blank)
                } else {
                    binding.editTextName.error = null
                }
            }
        }
    }

    private fun setupSaveButton() {
        with(binding) {
            buttonSave.setOnClickListener {
                if (validateData()) {
                    val gender = toggleGender.isChecked
                    val birthday = LocalDateTime.of(
                        spinnerYear.selectedItem?.toString()?.toInt() ?: 0,
                        spinnerMonth.selectedItemPosition + 1,
                        spinnerDay.selectedItem?.toString()?.toInt() ?: 1,
                        1,
                        1
                    )

                    val city = editTextCity.text.toString().trim()
                    val country = editTextCountry.text.toString().trim()
                    val name = editTextName.text.toString().trim()
                    val sport = (spinnerSport.selectedItem as Sport)

                    if (viewModel.athlete.value == null)
                        viewModel.addAthlete(name, city, country, gender, sport.id, birthday)
                    else
                        viewModel.updateAthlete(
                            args.athleteId,
                            name,
                            city,
                            country,
                            gender,
                            sport,
                            birthday
                        )
                }
            }
        }
    }

    private fun validateData(): Boolean {
        var pass = true

        with(binding) {
            val city = editTextCity.text.toString()
            val country = editTextCountry.text.toString()
            val name = editTextName.text.toString()

            if (editTextCity.error != null) {
                pass = false
            }
            if (editTextCountry.error != null) {
                pass = false
            }
            if (editTextName.error != null) {
                pass = false
            }

            if (city.isBlank()) {
                binding.editTextCity.error = getString(R.string.error_blank)
                pass = false
            }
            if (country.isBlank()) {
                editTextCountry.error = getString(R.string.error_blank)
                pass = false
            }
            if (name.isBlank()) {
                editTextName.error = getString(R.string.error_blank)
                pass = false
            }

            if (spinnerSport.selectedItem !is Sport) {
                binding.textError.text = getString(R.string.error_no_sport)
                pass = false
            }
        }
        return pass
    }

    private fun setupGenderToggle() {
        binding.toggleGender.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.imagePerson.setImageResource(R.drawable.male)
                viewModel.getSports(Gender.MALE)
            } else {
                binding.imagePerson.setImageResource(R.drawable.female)
                viewModel.getSports(Gender.FEMALE)
            }
        }
    }

    private fun setupBirthday() {

        val yearNow = LocalDateTime.now().year

        val years = mutableListOf<String>()
        val months = resources.getString(R.string.months).split(",")

        repeat(100) {
            years.add("${yearNow - it}")
        }

        with(binding) {
            spinnerYear.adapter = DateAdapter(requireContext(), R.layout.item_spinner, years)
            spinnerMonth.adapter = DateAdapter(requireContext(), R.layout.item_spinner, months)

            spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val year = LocalDateTime.of(
                        spinnerYear.selectedItem?.toString()?.toInt() ?: 0,
                        spinnerMonth.selectedItemPosition + 1,
                        1,
                        1,
                        1
                    )
                    spinnerDay.adapter =
                        DateAdapter(
                            requireContext(),
                            R.layout.item_spinner,
                            viewModel.getDays(year)
                        )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val year = LocalDateTime.of(
                        spinnerYear.selectedItem?.toString()?.toInt() ?: 0,
                        position + 1,
                        1,
                        1,
                        1
                    )
                    spinnerDay.adapter =
                        DateAdapter(
                            requireContext(),
                            R.layout.item_spinner,
                            viewModel.getDays(year)
                        )
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }
    }

    private fun setupSportsSpinner() {
        if (args.athleteId < 0)
            viewModel.getSports(Gender.FEMALE)
    }

    private fun setupObservers() {
        with(binding) {
            viewModel.athlete.observe(viewLifecycleOwner, {

                viewModel.getSports(it.gender)

                editTextName.setText(
                    it.name,
                    TextView.BufferType.EDITABLE
                )
                editTextCity.setText(
                    it.city,
                    TextView.BufferType.EDITABLE
                )
                editTextCountry.setText(
                    it.country.displayCountry,
                    TextView.BufferType.EDITABLE
                )

                toggleGender.isChecked = it.gender == Gender.MALE

                if (it.gender == Gender.MALE) {
                    imagePerson.setImageResource(R.drawable.male)
                } else {
                    imagePerson.setImageResource(R.drawable.female)
                }

                val year = (spinnerYear.adapter as DateAdapter).getItemPosition(
                    "${it.birthday.year}"
                )
                val day =
                    (spinnerDay.adapter as DateAdapter).getItemPosition("${it.birthday.dayOfMonth}")

                spinnerYear.setSelection(year)
                spinnerMonth.setSelection(it.birthday.monthValue - 1)
                spinnerDay.setSelection(day)
            })

            viewModel.isDataLoading.observe(viewLifecycleOwner, {
                progressLoading.isVisible = it
            })

            viewModel.saveSuccessful.observe(viewLifecycleOwner, {
                if (it) {
                    val navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

                    //This will trigger the AthletesFragment to refresh.
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        AthletesFragment.ATHLETES_REFRESH_KEY,
                        true
                    )
                    navController.navigateUp()
                }
            })

            viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
                showErrorPopup(it)
            })

            viewModel.sports.observe(viewLifecycleOwner, {
                spinnerSport.adapter = SportsAdapter(requireContext(), it)

                viewModel.athlete.value?.let { athlete ->
                    spinnerSport.setSelection(it.indexOf(athlete.sport))
                }
            })
        }
    }

    private fun setupEditTextCity() {
        binding.editTextCity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = binding.editTextCity.editableText.toString()
                viewModel.checkCity(requireContext(), country) {
                    if (it == null) {
                        binding.editTextCity.error = getString(R.string.error_empty_city)
                    } else {
                        binding.editTextCity.error = null
                        binding.editTextCity.setText(
                            it.locality ?: it.adminArea,
                            TextView.BufferType.EDITABLE
                        )
                    }
                }
            }
        }
    }

    private fun setupEditTextCountry() {
        val countries = mutableListOf<String>()
        Locale.getAvailableLocales().forEach {
            if (!countries.contains(it.displayCountry)) {
                countries.add(it.displayCountry)
            }
        }

        binding.editTextCountry.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val country = binding.editTextCountry.editableText.toString()
                if (!countries.contains(country)) {
                    binding.editTextCountry.error = getString(R.string.error_empty_country)
                } else {
                    binding.editTextCountry.error = null
                }
            }
        }

        ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            countries
        ).also { adapter ->
            binding.editTextCountry.setAdapter(adapter)
        }
    }
}