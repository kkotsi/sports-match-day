package com.example.sports_match_day.ui.home.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentAddMatchesBinding
import com.example.sports_match_day.model.*
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.ui.base.SportsAdapter
import com.example.sports_match_day.ui.base.observeOnce
import com.example.sports_match_day.ui.home.HomeFragment
import com.example.sports_match_day.utils.PopupManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

/**
 * Created by Kristo on 23-Mar-21
 */
class MatchesManageFragment : BaseFragment() {
    private val args: MatchesManageFragmentArgs by navArgs()

    private val viewModel: MatchesManageViewModel by viewModel()

    lateinit var adapter: ContestantsAdapter
    lateinit var footer: AddContestantAdapter
    private var previousSport: Sport? = null
    private var matchDate = LocalDateTime.now()

    private var _binding: FragmentAddMatchesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddMatchesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpForEdit()
        setupDatePickerButton()
        setupEditTextCountry()
        setupEditTextCity()
        setupStadiumEditText()
        setupSportsSpinner()
        setupParticipantsRecycler()
        setupSaveButton()
        setupObservers()
        setupTimePickerButton()
    }

    private fun setUpForEdit() {
        if (args.matchId > -1) {
            viewModel.loadMatch(args.matchId)
        }
    }

    private fun setupTimePickerButton() {
        binding.buttonTimePicker.setOnClickListener {
            PopupManager.timePickerPopup(requireContext()) { hour, minute ->
                matchDate = matchDate.withHour(hour).withMinute(minute)
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                binding.buttonTimePicker.text = matchDate.format(formatter)
            }
        }
    }

    private fun validateData(): Boolean {
        var pass = true
        with(binding) {
            val city = editTextCity.text.toString()
            val country = editTextCountry.text.toString()
            val stadium = editTextStadium.text.toString()

            if (editTextCity.error != null) {
                pass = false
            }
            if (editTextCountry.error != null) {
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

            if (previousSport == null) {
                textError.text = getString(R.string.error_no_sport)
            } else {
                if (adapter.participants.size != previousSport?.participantCount) {
                    pass = false
                    textError.text =
                        String.format(
                            requireContext().resources.getString(R.string.error_not_enough_participants),
                            "${previousSport?.participantCount}"
                        )
                }

                if (adapter.participants.groupingBy { it.contestant?.id }.eachCount()
                        .filter { it.value > 1 }.isNotEmpty()
                ) {
                    pass = false
                    textError.text = getString(R.string.error_duplicate_participant)
                }
            }
        }
        return pass
    }

    private fun setupObservers() {
        with(binding) {
            viewModel.match.observe(viewLifecycleOwner, {
                previousSport = it.sport ?: spinnerSport.selectedItem as? Sport
                viewModel.sports.value?.indexOf(it.sport)?.let { position ->
                    spinnerSport.setSelection(position)
                }
                editTextStadium.setText(
                    it.stadium,
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
                matchDate = it.date
                adapter.participants.clear()
                adapter.participants.addAll(it.participants)

                val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy")
                buttonDatePicker.text = matchDate.format(formatter)

                val formatter1 = DateTimeFormatter.ofPattern("HH:mm")
                buttonTimePicker.text = matchDate.format(formatter1)

                adapter.notifyDataSetChanged()
                footer.refresh(adapter.itemCount, previousSport)
            })

            viewModel.isDataLoading.observe(viewLifecycleOwner, {
                progressLoading.isVisible = it
            })

            viewModel.saveSuccessful.observe(viewLifecycleOwner, {
                if (it) {
                    val navController =
                        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                    //This will trigger the SportsFragment to refresh.
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        HomeFragment.HOME_REFRESH_KEY,
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
                viewModel.match.value?.let { match ->
                    spinnerSport.setSelection(it.indexOf(match.sport))
                }
            })
        }
    }

    private fun setupSaveButton() {
        binding.buttonSave.setOnClickListener {
            if (viewModel.isDataLoading.value == true) return@setOnClickListener

            if (validateData()) {
                val sport = binding.spinnerSport.selectedItem as Sport
                val city = binding.editTextCity.text.toString()
                val country = binding.editTextCountry.text.toString()
                val stadium = binding.editTextStadium.text.toString()
                val participants = adapter.participants

                if (viewModel.match.value != null) {
                    viewModel.updateMatch(
                        args.matchId,
                        matchDate,
                        city,
                        country,
                        stadium,
                        sport,
                        participants
                    )
                } else
                    viewModel.addMatch(
                        sport,
                        city,
                        country,
                        stadium,
                        matchDate,
                        adapter.participants
                    )
            }
        }
    }

    private fun setupParticipantsRecycler() {

        binding.recyclesParticipants.layoutManager = LinearLayoutManager(requireContext())

        val participants = mutableListOf<Participant>()

        adapter = ContestantsAdapter(participants)
        val header = AddContestantAdapter(false) {}
        footer = AddContestantAdapter(true) {
            binding.textError.text = ""
            viewModel.contestants.observeOnce(viewLifecycleOwner, {
                val contestants = mutableListOf<String>()
                it.forEach { contestant ->
                    contestants.add(contestant.name)
                }
                val hint =
                    if (previousSport?.type == SportType.TEAM) getString(R.string.squad) else getString(
                        R.string.solo
                    )

                PopupManager.contestantPickerPopup(
                    requireContext(),
                    hint,
                    contestants
                ) { contestantName ->

                    if (previousSport?.participantCount ?: 0 > adapter.participants.size) {
                        val contestant = it.find { contestant -> contestant.name == contestantName }
                        contestant?.let {
                            adapter.participants.add(Participant(contestant, 0.0))
                            adapter.notifyItemInserted(adapter.participants.size - 1)

                            footer.refresh(adapter.itemCount, previousSport)

                            if (adapter.participants.size == 1) {
                                refreshData()
                            }
                        }
                    }
                }
            })

            if (viewModel.contestants.value == null)
                viewModel.getContestants(previousSport)

        }
        binding.recyclesParticipants.adapter = ConcatAdapter(header, adapter, footer)

        val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (adapter.participants.size > 0) {
                    adapter.participants.removeAt(position)
                    adapter.notifyItemRemoved(position)
                } else {
                    adapter.notifyDataSetChanged()
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclesParticipants)

    }

    private fun setupSportsSpinner() {
        binding.spinnerSport.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSport = binding.spinnerSport.selectedItem as Sport
                if (selectedSport.id != previousSport?.id) {
                    refreshData()
                    selectSport(selectedSport)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        viewModel.getSports()
    }

    private fun refreshData() {
        with(binding) {
            //If the Sport is a team sport, auto-fill the stadium
            if (previousSport?.type == SportType.TEAM) {
                if(adapter.participants.isNotEmpty()) {
                    val firstSquad = adapter.participants.first().contestant as Squad
                    if (editTextStadium.text.toString().isEmpty())
                        editTextStadium.setText(
                            firstSquad.stadium,
                            TextView.BufferType.EDITABLE
                        )
                    if (editTextCity.text.toString().isEmpty())
                        editTextCity.setText(
                            firstSquad.city,
                            TextView.BufferType.EDITABLE
                        )
                    if (editTextCountry.text.toString().isEmpty())
                        editTextCountry.setText(
                            firstSquad.country.displayCountry,
                            TextView.BufferType.EDITABLE
                        )
                }
            } else {
                editTextStadium.setText(
                    "",
                    TextView.BufferType.EDITABLE
                )
                editTextCity.setText(
                    "",
                    TextView.BufferType.EDITABLE
                )
                editTextCountry.setText(
                    "",
                    TextView.BufferType.EDITABLE
                )
            }
        }
    }

    private fun selectSport(selectedSport: Sport) {
        viewModel.contestants.value = null
        adapter.participants.clear()
        adapter.notifyDataSetChanged()

        footer.refresh(adapter.itemCount, previousSport)

        previousSport = selectedSport
    }

    private fun setupEditTextCity() {
        binding.editTextCity.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = binding.editTextCity.editableText.toString()
                viewModel.checkCity(requireContext(), city) {
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

    private fun setupDatePickerButton() {
        binding.buttonDatePicker.setOnClickListener {
            PopupManager.datePickerPopup(requireContext()) {
                matchDate = matchDate.withYear(it.year).withMonth(it.monthValue)
                    .withDayOfMonth(it.dayOfMonth)
                binding.buttonDatePicker.text = it.toString().substring(0, 10)
            }
        }
        binding.buttonDatePicker.text = LocalDateTime.now().toString().substring(0, 10)
    }
}