package com.example.sports_match_day.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var loader: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoader()
        setupObservers()
        setupCalendar()
        setupAddButton()
    }

    private fun setupAddButton(){
        view?.findViewById<FloatingActionButton>(R.id.fab_add)?.let{
            it.setOnClickListener {
               viewModel.addMatch()
            }
        }
    }

    private fun setupObservers(){
        viewModel.matches.observe(viewLifecycleOwner, {
            it?.let {

                it.addWeakCallback(null, object: PagedList.Callback() {
                    override fun onChanged(position: Int, count: Int) {
                        //refreshLayout.isRefreshing = false
                        setUpEvent()
                    }
                    override fun onInserted(position: Int, count: Int) {
                        //refreshLayout.isRefreshing = false
                        loader.visibility = View.INVISIBLE
                        setUpEvent()
                    }
                    override fun onRemoved(position: Int, count: Int) {
                    }
                })

                setUpEvent()
                recyclerSetup(it)
            }
        })

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            if(it){
                loader.visibility = View.VISIBLE
                loader.animate()
            }else {
                loader.visibility = View.INVISIBLE
            }
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            viewModel.invalidatedData()
        })
    }

    private fun setupLoader(){
        view?.let { v ->
            loader = v.findViewById(R.id.progress_loading)
        }
    }

    private fun setupCalendar(){
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

    private fun setUpEvent(){
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

    private fun recyclerSetup(matches: PagedList<Match>){
        view?.let {
            recyclerMatches = it.findViewById(R.id.recycler_matches)
            recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

            recyclerMatches.adapter = MatchAdapter{ match ->
                selectDate(match.date)
            }

            (recyclerMatches.adapter as MatchAdapter).submitList(matches)

            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    viewModel.removeMatch((recyclerMatches.adapter as? MatchAdapter)?.getMatch(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerMatches)
        }
    }

    private fun selectDate(date: LocalDateTime){
        val calendar: Calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, date.year)
        calendar.set(Calendar.MONTH, date.month.value - 1)
        calendar.set(Calendar.DATE, date.dayOfMonth)

        calendarView.setDate(calendar)
    }
}