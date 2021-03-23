package com.example.sports_match_day.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.sports_match_day.R
import com.example.sports_match_day.firebase.ExampleLoadStateAdapter
import com.example.sports_match_day.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*


/**
 * Created by Kristo on 05-Mar-21
 */
class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var recyclerMatches: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var textTotal: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var adapter: MatchAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTotalText()
        recyclerSetup()
        setupCalendar()
        setupAddButton()
        setupRefreshLayout()
        setupObservers()
    }

    private fun setupAddButton() {
        view?.findViewById<FloatingActionButton>(R.id.fab_add)?.let {
            it.setOnClickListener {
                viewModel.addMatch()
            }
        }
    }

    private fun setupRefreshLayout() {
        view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)?.let {
            refreshLayout = it
        }

        refreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupObservers() {
        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            refreshLayout.isRefreshing = it
        })

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                refreshCount()
                refreshLayout.isRefreshing = (loadStates.refresh is LoadState.Loading)

                if (loadStates.refresh is LoadState.Error) {
                    showErrorPopup((loadStates.refresh as? LoadState.Error)?.error ?: Throwable())
                } else if (loadStates.refresh is LoadState.NotLoading) {
                    setUpEvent()
                }
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
        if(requireActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

            // A simple way to get data from another fragment with NavController https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                HomeFragment.HOME_REFRESH_KEY
            )?.observe(
                viewLifecycleOwner
            ) {
                adapter.refresh()
            }
        }
    }


    private fun setupTotalText() {
        view?.findViewById<TextView>(R.id.text_matches_total)?.let {
            textTotal = it
        }
    }

    private fun setupCalendar() {
        view?.let { v ->
            calendarView = v.findViewById(R.id.calendarView)

            //Set current date
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(
                    Calendar.DATE
                )
            )

            calendarView.setDate(calendar)
        }
    }

    private fun setUpEvent() {
        val events = mutableListOf<EventDay>()

        calendarView.setEvents(mutableListOf())
        viewModel.getEventDates().forEach { date ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month.value - 1)
            calendar.set(Calendar.DATE, date.dayOfMonth)

            events.add(EventDay(calendar, R.drawable.event))
        }

        calendarView.setEvents(events)
    }

    private fun recyclerSetup() {
        view?.let {
            recyclerMatches = it.findViewById(R.id.recycler_matches)
            recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

            adapter = MatchAdapter { match ->
                selectDate(match.date)
            }

            recyclerMatches.adapter = adapter.withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter { adapter.refresh() },
                footer = ExampleLoadStateAdapter { adapter.refresh() }
            )

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
            itemTouchHelper.attachToRecyclerView(recyclerMatches)
        }
    }

    private fun selectDate(date: LocalDateTime) {
        val calendar: Calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.month.value - 1)
        calendar.set(Calendar.DATE, date.dayOfMonth)

        calendarView.setDate(calendar)
    }

    private fun refreshCount() {
        val total = adapter.itemCount
        textTotal.text =
            String.format(requireContext().resources.getString(R.string.total_squads), "$total")
    }
    companion object{
        const val HOME_REFRESH_KEY = "home_resume_key"
    }
}