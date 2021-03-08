package com.example.sports_match_day.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.example.sports_match_day.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 * Created by Kristo on 05-Mar-21
 */
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var recyclerMatches: RecyclerView
    private lateinit var calendarView: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.text.observe(viewLifecycleOwner, {

        })
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerSetup()
        calendarSetup()
    }

    private fun calendarSetup(){
        view?.let { v ->
            calendarView = v.findViewById(R.id.calendarView)

            //Set current date
            val calendar: Calendar = Calendar.getInstance()
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE))

            calendarView.setDate(calendar)
        }
    }

    private fun setUpEvent(){
        val calendar: Calendar = Calendar.getInstance()
        val events = mutableListOf<EventDay>()
        events.add(EventDay(calendar, R.drawable.event))
        calendarView.setEvents(events)
    }

    private fun recyclerSetup(){
        view?.let {
            recyclerMatches = it.findViewById(R.id.recycler_matches)
            recyclerMatches.layoutManager = LinearLayoutManager(requireContext())
            val countries = listOf("be", "al", "ar", "au")
            recyclerMatches.adapter = MatchAdapter(requireContext(), countries)
        }
    }
}