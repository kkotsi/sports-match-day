package com.example.sports_match_day.ui.home

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.sports_match_day.R
import com.example.sports_match_day.controllers.DecoupleAdapter
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.utils.RawManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.LocalDateTime
import java.util.*


/**
 * Created by Kristo on 05-Mar-21
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var recyclerMatches: RecyclerView
    private lateinit var calendarView: CalendarView
    private lateinit var loader: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBarSetup()
        setupObservers()
        calendarSetup()
        viewModel.loadData()
    }

    private fun setupObservers(){
        viewModel.matches.observe(viewLifecycleOwner, {
            repeat(it.size) {
                setUpEvent()
            }
            recyclerSetup(it)
        })

        viewModel.doneLoading.observe(viewLifecycleOwner, { doneLoading ->
            if(doneLoading){
                loader.visibility = View.INVISIBLE
            }else {
                loader.visibility = View.VISIBLE
                loader.animate()
            }
        })
    }
    private fun progressBarSetup(){

        view?.let { v ->
            loader = v.findViewById(R.id.progress_loading)
        }
    }

    private fun calendarSetup(){
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

        viewModel.getEventDates().forEach { date ->
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, date.year)
            calendar.set(Calendar.MONTH, date.month.value - 1)
            calendar.set(Calendar.DATE, date.dayOfMonth)

            events.add(EventDay(calendar, R.drawable.event))
        }

        calendarView.setEvents(events)
    }

    private fun recyclerSetup(matches: List<Match>){
        view?.let {
            recyclerMatches = it.findViewById(R.id.recycler_matches)
            recyclerMatches.layoutManager = LinearLayoutManager(requireContext())

            recyclerMatches.adapter = MatchAdapter(requireContext(), matches){ match ->
                selectDate(match.date)
            }
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