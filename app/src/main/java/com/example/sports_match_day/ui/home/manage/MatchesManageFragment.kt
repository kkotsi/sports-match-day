package com.example.sports_match_day.ui.home.manage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.*
import com.example.sports_match_day.ui.athletes.manage.SportsAdapter
import com.example.sports_match_day.ui.base.BaseFragment
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
    private lateinit var citiesEditTextView: AutoCompleteTextView
    private lateinit var countriesEditTextView: AutoCompleteTextView
    private lateinit var datePickerButton: Button
    private lateinit var timePickerButton: Button
    private lateinit var buttonSave: Button
    private lateinit var loader: ProgressBar
    private lateinit var recyclerParticipants: RecyclerView
    private lateinit var sportsSpinner: Spinner
    private lateinit var stadiumEditTextView: AutoCompleteTextView
    private lateinit var errorTextView: TextView

    lateinit var adapter: ContestantsAdapter
    lateinit var footer: AddContestantAdapter
    private var previousSport: Sport? = null
    private var matchDate = LocalDateTime.now()
    private var matchId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_matches, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //HomeFragmentDirections.actionNavHomeToNavMatchAdd(1L)
        //val matchId = args
        matchId = args.matchId
        setUpForEdit()
        setUpErrorTextView()
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
        if(matchId > -1) {
            viewModel.loadMatch(matchId)
        }
    }

    private fun setUpErrorTextView() {
        errorTextView = requireView().findViewById(R.id.text_error)
    }

    private fun setupTimePickerButton() {
        timePickerButton = requireView().findViewById(R.id.button_time_picker)
        timePickerButton.setOnClickListener {
            PopupManager.timePickerPopup(requireContext()) { hour, minute ->
                matchDate = matchDate.withHour(hour).withMinute(minute)
                val formatter = DateTimeFormatter.ofPattern("HH:mm")
                timePickerButton.text = matchDate.format(formatter)
            }
        }
    }

    private fun validateData(): Boolean {
        var pass = true

        val city = citiesEditTextView.text.toString()
        val country = countriesEditTextView.text.toString()
        val stadium = stadiumEditTextView.text.toString()

        if (citiesEditTextView.error != null) {
            pass = false
        }
        if (countriesEditTextView.error != null) {
            pass = false
        }
        if (stadiumEditTextView.error != null) {
            pass = false
        }

        if (stadium.isBlank()) {
            stadiumEditTextView.error = getString(R.string.error_blank)
            pass = false
        }
        if (city.isBlank()) {
            citiesEditTextView.error = getString(R.string.error_blank)
            pass = false
        }
        if (country.isBlank()) {
            countriesEditTextView.error = getString(R.string.error_blank)
            pass = false
        }

        if (previousSport == null) {
            errorTextView.text = getString(R.string.error_no_sport)
        } else {
            if (adapter.participants.size != previousSport?.participantCount) {
                pass = false
                errorTextView.text =
                    String.format(
                        requireContext().resources.getString(R.string.error_not_enough_participants),
                        "${previousSport?.participantCount}"
                    )
            }

            if (adapter.participants.groupingBy { it.contestant?.id }.eachCount()
                    .filter { it.value > 1 }.isNotEmpty()
            ) {
                pass = false
                errorTextView.text = getString(R.string.error_duplicate_participant)
            }
        }

        return pass
    }

    private fun setupObservers() {
        loader = requireView().findViewById(R.id.progress_loading)

        viewModel.match.observe(viewLifecycleOwner, {
            previousSport = it.sport
            viewModel.sports.value?.indexOf(it.sport)?.let{
                sportsSpinner.setSelection(it)
            }
            stadiumEditTextView.setText(
                it.stadium,
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
            matchDate = it.date
            adapter.participants.clear()
            adapter.participants.addAll(it.participants)

            val formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy")
            datePickerButton.text = matchDate.format(formatter)

            val formatter1 = DateTimeFormatter.ofPattern("HH:mm")
            timePickerButton.text = matchDate.format(formatter1)

            adapter.notifyDataSetChanged()
            footer.refresh(adapter.itemCount, previousSport)
        })

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            if (it)
                loader.visibility = View.VISIBLE
            else
                loader.visibility = View.INVISIBLE
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
            sportsSpinner.adapter = SportsAdapter(requireContext(), it)
            viewModel.match.value?.let { match ->
                sportsSpinner.setSelection(it.indexOf(match.sport))
            }
        })
    }

    private fun setupSaveButton() {
        buttonSave = requireView().findViewById(R.id.button_save)
        buttonSave.setOnClickListener {
            if (viewModel.isDataLoading.value == true) return@setOnClickListener

            if (validateData()) {
                val sport = sportsSpinner.selectedItem as Sport
                val city = citiesEditTextView.text.toString()
                val country = countriesEditTextView.text.toString()
                val stadium = stadiumEditTextView.text.toString()
                val participants = adapter.participants

                if(viewModel.match.value != null) {
                    viewModel.updateMatch(matchId,matchDate,city,country,stadium,sport,participants)
                }else
                    viewModel.addMatch(sport, city, country, stadium, matchDate, adapter.participants)
            }
        }
    }

    private fun setupParticipantsRecycler() {
        recyclerParticipants = requireActivity().findViewById(R.id.recycles_participants)

        recyclerParticipants.layoutManager = LinearLayoutManager(requireContext())

        val participants = mutableListOf<Participant>()

        adapter = ContestantsAdapter(participants)
        val header = AddContestantAdapter(false) {}
        footer = AddContestantAdapter(true) {
            errorTextView.text = ""
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

            if (viewModel.contestants.value == null) viewModel.getContestants(previousSport)

        }
        recyclerParticipants.adapter = ConcatAdapter(header, adapter, footer)

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
                adapter.participants.removeAt(position)
                adapter.notifyItemRemoved(position)
                footer.refresh(adapter.itemCount, previousSport)
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerParticipants)

    }

    private fun setupSportsSpinner() {
        sportsSpinner = requireView().findViewById(R.id.spinner_sport)
        sportsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSport = sportsSpinner.selectedItem as Sport
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
        //If the Sport is a team sport, auto-fill the stadium
        if (previousSport?.type == SportType.TEAM && adapter.participants.isNotEmpty()) {
            val firstSquad = adapter.participants.first().contestant as Squad
            stadiumEditTextView.setText(
                firstSquad.stadium,
                TextView.BufferType.EDITABLE
            )
            citiesEditTextView.setText(
                firstSquad.city,
                TextView.BufferType.EDITABLE
            )
            countriesEditTextView.setText(
                firstSquad.country.displayCountry,
                TextView.BufferType.EDITABLE
            )
        } else {
            stadiumEditTextView.setText(
                "",
                TextView.BufferType.EDITABLE
            )
            citiesEditTextView.setText(
                "",
                TextView.BufferType.EDITABLE
            )
            countriesEditTextView.setText(
                "",
                TextView.BufferType.EDITABLE
            )
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
        citiesEditTextView = requireView().findViewById(R.id.editText_city)

        citiesEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val city = citiesEditTextView.editableText.toString()
                viewModel.checkCity(requireContext(), city) {
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

    private fun setupStadiumEditText() {
        stadiumEditTextView = requireView().findViewById(R.id.editText_stadium)
        stadiumEditTextView.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val stadium = stadiumEditTextView.editableText.toString()
                if (stadium.isBlank()) {
                    stadiumEditTextView.error = getString(R.string.error_blank)
                } else {
                    stadiumEditTextView.error = null
                }
            }
        }
    }

    private fun setupEditTextCountry() {
        countriesEditTextView = requireView().findViewById(R.id.editText_country)

        val countries = mutableListOf<String>()
        Locale.getAvailableLocales().forEach {
            if (!countries.contains(it.displayCountry)) {
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

    private fun setupDatePickerButton() {
        datePickerButton = requireView().findViewById(R.id.button_date_picker)
        datePickerButton.setOnClickListener {
            PopupManager.datePickerPopup(requireContext()) {
                matchDate = matchDate.withYear(it.year).withMonth(it.monthValue)
                    .withDayOfMonth(it.dayOfMonth)
                datePickerButton.text = it.toString().substring(0, 10)
            }
        }
        datePickerButton.text = LocalDateTime.now().toString().substring(0, 10)
    }
}