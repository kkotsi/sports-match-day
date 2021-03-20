package com.example.sports_match_day.ui.sports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsFragment : BaseFragment() {

    private val viewModel: SportsViewModel by viewModel()
    private lateinit var recyclerSports: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTotalText()
        recyclerSetup()
        setupAddButton()
        setupRefreshLayout()
        setupObservers()
    }

    private fun setupRefreshLayout() {
        view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)?.let{
            refreshLayout = it
        }

        refreshLayout.setOnRefreshListener {
            (recyclerSports.adapter as SportsAdapter).refresh()
        }
    }

    private fun setupAddButton(){
        view?.findViewById<FloatingActionButton>(R.id.fab_add)?.let{
            it.setOnClickListener {

                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.action_nav_sports_to_nav_sports_add)
            }
        }
    }

    private fun setupTotalText(){
        view?.findViewById<TextView>(R.id.text_total)?.let{
            textTotal = it
        }
    }

    private fun setupObservers(){

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            refreshLayout.isRefreshing = it
        })

        lifecycleScope.launchWhenCreated {
            (recyclerSports.adapter as SportsAdapter).loadStateFlow.collectLatest { loadStates ->
                refreshCount()
                refreshLayout.isRefreshing = (loadStates.refresh is LoadState.Loading)
            }
        }

        lifecycleScope.launchWhenCreated  {
            viewModel.pagedSports.collectLatest {
                (recyclerSports.adapter as SportsAdapter).submitData(it)
            }
        }

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            (recyclerSports.adapter as SportsAdapter).refresh()
        })
    }

    private fun recyclerSetup(){
        view?.let {
            recyclerSports = it.findViewById(R.id.recycler_sports)
            recyclerSports.layoutManager = LinearLayoutManager(requireContext())

            recyclerSports.adapter = SportsAdapter()

            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    viewModel.removeSport((recyclerSports.adapter as? SportsAdapter)?.getSport(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerSports)
        }
    }

    private fun refreshCount(){
        val total = recyclerSports.adapter?.itemCount ?: 0
        textTotal.text =  String.format(requireContext().resources.getString(R.string.total_sports), "$total")
    }
}