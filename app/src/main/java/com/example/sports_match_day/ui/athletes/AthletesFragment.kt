package com.example.sports_match_day.ui.athletes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesFragment : Fragment() {

    private val viewModel: AthletesViewModel by viewModel()
    private lateinit var recyclerAthletes: RecyclerView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_athletes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerSetup()
        setupObservers()
    }

    private fun setupObservers(){
        viewModel.pagedAthletes.observe(viewLifecycleOwner, {
            (recyclerAthletes.adapter as AthletesAdapter).submitList(it)
        })
    }

    private fun recyclerSetup(){
        view?.let {
            recyclerAthletes = it.findViewById(R.id.recycler_athletes)
            recyclerAthletes.layoutManager = LinearLayoutManager(requireContext())

            recyclerAthletes.adapter = AthletesAdapter(requireContext())
        }
    }
}