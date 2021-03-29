package com.example.sports_match_day.ui.squads.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentAddSquadBinding
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.base.DateAdapter
import com.example.sports_match_day.ui.base.SportsAdapter
import com.example.sports_match_day.ui.squads.SquadsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Kristo on 14-Mar-21
 */
class SquadsManageFragment : BaseFragment() {
    private val args: SquadsManageFragmentArgs by navArgs()
    private val viewModel: SquadsManageViewModel by viewModel()

    private var _binding: FragmentAddSquadBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSquadBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupForEdit()
        setupObservers()
        setupBirthday()
        setupEditTextCountry()
        setupEditTextCity()
        setupSaveButton()
        setupNameEditText()
        setupStadiumEditText()
        setupSportsSpinner()
        setupGender()
    }

    private fun setupGender(){
        binding.toggleGender.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.getSports(Gender.MALE)
            }
            else {
                viewModel.getSports(Gender.FEMALE)
            }
        }
    }

    private fun setupForEdit() {
        if (args.squadId > -1) {
            viewModel.loadSquad(args.squadId)
        }
    }

    private fun setupStadiumEditText() {
        binding.editTextStadium.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val stadium = binding.editTextStadium.editableText.toString()
                if (stadium.isBlank()) {
                    binding.editTextStadium.error = getString(R.string.error_blank)
                } else {
                    binding.editTextStadium.error = null
                }
            }
        }
    }

    private fun setupNameEditText() {
        binding.editTextName.addTextChangedListener {
            binding.textName.text = it?.toString()
        }
        binding.editTextName.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val name = binding.editTextName.editableText.toString()
                if (name.isBlank()) {
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
                val birthday = LocalDateTime.of(
                    spinnerYear.selectedItem.toString().toInt(),
                    spinnerMonth.selectedItemPosition  + 1,
                    spinnerDay.selectedItem?.toString()?.toInt() ?: 1,
                    1,
                    1
                )

                val city = editTextCity.text.toString().trim()
                val country = editTextCountry.text.toString().trim()
                val name = editTextName.text.toString().trim()
                val stadium = editTextStadium.text.toString().trim()
                val sport = spinnerSport.selectedItem as Sport
                val gender = binding.toggleGender.isChecked

                if (validateData()) {
                    if (viewModel.squad.value == null)
                        viewModel.addSquad(name, city, country, stadium, sport.id, birthday, gender)
                    else
                        viewModel.updateSquad(
                            args.squadId,
                            name,
                            city,
                            country,
                            stadium,
                            sport,
                            birthday,
                            gender
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
            val stadium = editTextStadium.text.toString()

            if (editTextCity.error != null) {
                pass = false
            }
            if (editTextCountry.error != null) {
                pass = false
            }
            if (editTextName.error != null) {
                pass = false
            }
            if (editTextStadium.error != null) {
                pass = false
            }

            if (stadium.isBlank()) {
                editTextStadium.error = getString(R.string.error_blank)
                pass = false
            }
            if (city.isBlank()) {
                editTextCity.error = getString(R.string.error_blank)
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
        }
        return pass
    }

    private fun setupBirthday() {
        val yearNow = LocalDateTime.now().year

        val years = mutableListOf<String>()
        val months = resources.getString(R.string.months).split(",")

        repeat(150) {
            years.add("${yearNow - it}")
        }

        binding.spinnerYear.adapter = DateAdapter(requireContext(), R.layout.item_spinner, years)
        binding.spinnerMonth.adapter = DateAdapter(requireContext(), R.layout.item_spinner_text, months)

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    binding.spinnerYear.selectedItem?.toString()?.toInt() ?: 0,
                    binding.spinnerMonth.selectedItemPosition + 1,
                    1,
                    1,
                    1
                )
                binding.spinnerDay.adapter =
                    DateAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinnerMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val year = LocalDateTime.of(
                    binding.spinnerYear.selectedItem?.toString()?.toInt() ?: 0,
                    position + 1,
                    1,
                    1,
                    1
                )
                binding.spinnerDay.adapter =
                    DateAdapter(requireContext(), R.layout.item_spinner, viewModel.getDays(year))
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSportsSpinner() {
        if(args.squadId < 0)
            viewModel.getSports(Gender.FEMALE)
    }

    private fun setupObservers() {
        with(binding) {
            viewModel.squad.observe(viewLifecycleOwner, {

                viewModel.getSports(it.gender)

                binding.toggleGender.isChecked = it.gender == Gender.MALE

                viewModel.sports.value?.indexOf(it.sport)?.let { position ->
                    spinnerSport.setSelection(position)
                }

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
                editTextStadium.setText(
                    it.stadium,
                    TextView.BufferType.EDITABLE
                )
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
                    //This will trigger the SquadsFragment to refresh.
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        SquadsFragment.SQUADS_REFRESH_KEY,
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
                viewModel.squad.value?.let { squad ->
                    spinnerSport.setSelection(it.indexOf(squad.sport))
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