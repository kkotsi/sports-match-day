package com.example.sports_match_day.ui.squads


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
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsFragment : BaseFragment() {

    private val viewModel: SquadsViewModel by viewModel()
    private lateinit var recyclerSquads: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_squads, container, false)
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
            (recyclerSquads.adapter as? SquadsAdapter)?.refresh()
        }
    }

    private fun setupAddButton(){
        view?.findViewById<FloatingActionButton>(R.id.fab_add)?.let{
            buttonAdd = it
            buttonAdd.setOnClickListener {

                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.action_nav_squads_to_nav_squads_add)
            }
        }
    }

    private fun setupTotalText(){
        view?.findViewById<TextView>(R.id.text_squads_total)?.let{
            textTotal = it
        }
    }

    private fun setupObservers(){
        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            refreshLayout.isRefreshing = it
        })

        lifecycleScope.launch {
            (recyclerSquads.adapter as SquadsAdapter).loadStateFlow.collectLatest { loadStates ->
                refreshCount()
                refreshLayout.isRefreshing = (loadStates.refresh is LoadState.Loading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated  {
            viewModel.pagedSquads.collectLatest {
                (recyclerSquads.adapter as SquadsAdapter).submitData(it)
            }
        }

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            (recyclerSquads.adapter as SquadsAdapter).refresh()
        })
    }

    private fun recyclerSetup(){
        view?.let {
            recyclerSquads = it.findViewById(R.id.recycler_squads)
            recyclerSquads.layoutManager = LinearLayoutManager(requireContext())

            recyclerSquads.adapter = SquadsAdapter()

            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.absoluteAdapterPosition
                    viewModel.removeSquad((recyclerSquads.adapter as? SquadsAdapter)?.getSquad(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerSquads)
        }
    }

    private fun refreshCount(){
        val total = recyclerSquads.adapter?.itemCount ?: 0
        textTotal.text =  String.format(requireContext().resources.getString(R.string.total_squads), "$total")
    }
}