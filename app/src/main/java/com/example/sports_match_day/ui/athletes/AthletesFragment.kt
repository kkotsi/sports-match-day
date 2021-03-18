package com.example.sports_match_day.ui.athletes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation.findNavController
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sports_match_day.R
import com.example.sports_match_day.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class AthletesFragment : BaseFragment() {

    private val viewModel: AthletesViewModel by viewModel()
    private lateinit var recyclerAthletes: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var buttonAdd: FloatingActionButton
    private lateinit var loader: ProgressBar
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_athletes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupAddButton()
        setupLoader()
        setupTotalText()
        recyclerSetup()
        setupRefreshLayout()
    }

    private fun setupRefreshLayout() {
        view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)?.let{
            refreshLayout = it
        }

        refreshLayout.setOnRefreshListener {
            viewModel.invalidatedData()
        }
    }

    private fun setupAddButton(){
        view?.findViewById<FloatingActionButton>(R.id.fab_add)?.let{
            buttonAdd = it
            buttonAdd.setOnClickListener {

                val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.action_nav_athletes_to_nav_athletes_add)
            }
        }
    }

    private fun setupLoader(){
        view?.findViewById<ProgressBar>(R.id.progress_loading)?.let{
            loader = it
        }
    }

    private fun setupTotalText(){
        view?.findViewById<TextView>(R.id.text_total)?.let{
            textTotal = it
        }
    }

    private fun setupObservers(){

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            if(it){
                loader.visibility = View.VISIBLE
            }else{
                loader.visibility = View.INVISIBLE
            }
        })

        viewModel.pagedAthletes.observe(viewLifecycleOwner, {
            refreshCount()
            refreshLayout.isRefreshing = false
            it.addWeakCallback(null, object: PagedList.Callback() {
                override fun onChanged(position: Int, count: Int) {
                    refreshCount()
                    refreshLayout.isRefreshing = false
                }
                override fun onInserted(position: Int, count: Int) {
                    refreshCount()
                    loader.visibility = View.INVISIBLE
                    refreshLayout.isRefreshing = false
                }
                override fun onRemoved(position: Int, count: Int) {
                    refreshCount()
                }
            })
            (recyclerAthletes.adapter as AthletesAdapter).submitList(it)
            //recyclerAthletes.adapter?.notifyDataSetChanged()
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            viewModel.invalidatedData()
            //refreshCount()
        })
    }

    var positionRemoved = 0
    private fun recyclerSetup(){
        view?.let {
            recyclerAthletes = it.findViewById(R.id.recycler_athletes)
            recyclerAthletes.layoutManager = LinearLayoutManager(requireContext())

            recyclerAthletes.adapter = AthletesAdapter()

            val simpleItemTouchCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    val position = viewHolder.adapterPosition
                    positionRemoved = position
                    viewModel.removeAthlete((recyclerAthletes.adapter as? AthletesAdapter)?.getAthlete(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerAthletes)
        }
    }

    private fun refreshCount(){
        val total = recyclerAthletes.adapter?.itemCount ?: 0
        textTotal.text =  String.format(requireContext().resources.getString(R.string.total_athletes), "$total")
    }

    override fun onResume() {
        super.onResume()
        refreshCount()
    }
}