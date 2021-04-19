package com.example.sports_match_day.ui.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.EventDay
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentHomeBinding
import com.example.sports_match_day.firebase.ExampleLoadStateAdapter
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*


/**
 * Created by Kristo on 05-Mar-21
 */
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var adapter: MatchAdapter
    private var selectedMatchId: Int = -1

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerSetup()
        setupCalendar()
        setupAddButton()
        setupRefreshLayout()
        setupObservers()
        setupErrorButton()
    }

    private fun setupErrorButton(){
        binding.buttonError.isVisible = Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
        binding.buttonError.setOnClickListener {
            Prefs.putBoolean(PreferencesKeys.TEST_ERROR, true)
            Toast.makeText(requireContext(),"Scroll to test-crash", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupAddButton() {
        binding.fabAdd.setOnClickListener {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.action_nav_home_to_nav_match_add)
        }
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupObservers() {

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                refreshCount()

                if (loadStates.refresh is LoadState.Error) {
                    showErrorPopup((loadStates.refresh as? LoadState.Error)?.error ?: Throwable())
                } else if (loadStates.refresh is LoadState.NotLoading) {
                    setUpEvent()
                }
                binding.swipeRefreshLayout.isRefreshing = (loadStates.refresh is LoadState.Loading)
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.matches.collectLatest {
                adapter.submitData(it)
            }
        }

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            adapter.refresh()
        })

        //If the activity has been created the NavController will not be found, thus an exception will be thrown.
        if (requireActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

            // A simple way to get data from another fragment with NavController https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                HOME_REFRESH_KEY
            )?.observe(
                viewLifecycleOwner
            ) {
                adapter.refresh()
            }
        }
    }

    private fun setupCalendar() {
        //Set current date
        val calendar: Calendar = Calendar.getInstance()
        calendar.set(
            calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                Calendar.DATE
            )
        )

        binding.calendarView.setDate(calendar)
    }

    private fun setUpEvent() {
        val events = mutableListOf<EventDay>()

        binding.calendarView.setEvents(mutableListOf())
        viewModel.getEventDates().forEach { date ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month.value - 1)
            calendar.set(Calendar.DATE, date.dayOfMonth)

            events.add(EventDay(calendar, R.drawable.event))
        }

        binding.calendarView.setEvents(events)
    }

    private fun editMatch() {
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val action = HomeFragmentDirections.actionNavHomeToNavMatchAdd(selectedMatchId)
        navController.navigate(action)
    }

    private fun searchCity(stadium: String) {
        val uri = Uri.parse("geo:0,0?q=$stadium stadium")
        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun recyclerSetup() {
        binding.recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

        adapter = MatchAdapter({ match ->
            if (match.id == selectedMatchId) {
                editMatch()
            } else {
                selectDate(match.date)
            }
            selectedMatchId = match.id
        }) {
            searchCity(it)
        }

        binding.recyclerMatches.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ExampleLoadStateAdapter { adapter.refresh() },
            footer = ExampleLoadStateAdapter { adapter.refresh() }
        )

        binding.recyclerMatches.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabAdd.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0 && binding.fabAdd.isShown)
                    binding.fabAdd.hide()
            }
        })

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
                viewModel.removeMatch(
                    adapter.getMatch(
                        position
                    )
                )
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerMatches)

    }

    private fun selectDate(date: LocalDateTime) {
        val calendar: Calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.month.value - 1)
        calendar.set(Calendar.DATE, date.dayOfMonth)

        binding.calendarView.setDate(calendar)
    }

    private fun refreshCount() {
        binding.textMatchesTotal.isVisible = Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
        val total = adapter.itemCount
        binding.textMatchesTotal.text =
            String.format(requireContext().resources.getString(R.string.total_squads), "$total")
    }

    companion object {
        const val HOME_REFRESH_KEY = "home_resume_key"
    }
}