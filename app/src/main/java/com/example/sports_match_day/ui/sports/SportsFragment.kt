package com.example.sports_match_day.ui.sports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.navigation.Navigation
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
 * Created by Kristo on 11-Mar-21
 */
class SportsFragment : BaseFragment() {

    private val viewModel: SportsViewModel by viewModel()
    private lateinit var recyclerSports: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var loader: ProgressBar
    private lateinit var refreshLayout: SwipeRefreshLayout
    private var total = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sports, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLoader()
        setupTotalText()
        recyclerSetup()
        setupObservers()
        setupAddButton()
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
            it.setOnClickListener {

                val navController =
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
                navController.navigate(R.id.action_nav_sports_to_nav_sports_add)
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

        viewModel.pagedSports.observe(viewLifecycleOwner, {
            it.addWeakCallback(null, object: PagedList.Callback() {
                override fun onChanged(position: Int, count: Int) {
                    refreshCount()
                    refreshLayout.isRefreshing = false
                }
                override fun onInserted(position: Int, count: Int) {
                    refreshCount()
                    refreshLayout.isRefreshing = false
                    loader.visibility = View.INVISIBLE
                }
                override fun onRemoved(position: Int, count: Int) {
                    refreshCount()
                }
            })
            (recyclerSports.adapter as SportsAdapter).submitList(it)
        })

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            viewModel.invalidatedData()
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
                    val position = viewHolder.adapterPosition
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