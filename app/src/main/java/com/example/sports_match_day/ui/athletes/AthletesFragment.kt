package com.example.sports_match_day.ui.athletes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.sports_match_day.R
import com.example.sports_match_day.firebase.ExampleLoadStateAdapter
import com.example.sports_match_day.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var adapter: AthletesAdapter
    private lateinit var addButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_athletes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAddButton()
        setupLoader()
        setupTotalText()
        recyclerSetup()
        setupRefreshLayout()
        setupObservers()
    }

    private fun setupRefreshLayout() {
        view?.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)?.let {
            refreshLayout = it
        }

        refreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupAddButton() {
        addButton = requireView().findViewById(R.id.fab_add)

        addButton.setOnClickListener {
            val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.action_nav_athletes_to_nav_athletes_add)
        }
    }

    private fun setupLoader() {
        view?.findViewById<ProgressBar>(R.id.progress_loading)?.let {
            loader = it
        }
    }

    private fun setupTotalText() {
        view?.findViewById<TextView>(R.id.text_total)?.let {
            textTotal = it
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
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.pagedAthletes.collectLatest {
                adapter.submitData(it)
            }
        }

        viewModel.apiErrorMessage.observe(viewLifecycleOwner, {
            showErrorPopup(it)
        })

        viewModel.removeSuccessful.observe(viewLifecycleOwner, {
            adapter.refresh()
        })

        val navController = findNavController(requireActivity(), R.id.nav_host_fragment)

        // A simple way to get data from another fragment with NavController https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
        navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            ATHLETES_REFRESH_KEY
        )?.observe(
            viewLifecycleOwner
        ) {
            adapter.refresh()
        }
    }

    private fun recyclerSetup() {
        view?.let {
            recyclerAthletes = it.findViewById(R.id.recycler_athletes)
            recyclerAthletes.layoutManager = LinearLayoutManager(requireContext())

            adapter = AthletesAdapter()

            recyclerAthletes.adapter = adapter.withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter { adapter.refresh() },
                footer = ExampleLoadStateAdapter { adapter.refresh() }
            )

            recyclerAthletes.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        addButton.show()
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }

                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0 && addButton.isShown)
                        addButton.hide()
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
                    viewModel.removeAthlete(adapter.getAthlete(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerAthletes)
        }
    }

    private fun refreshCount() {
        val total = adapter.itemCount
        textTotal.text =
            String.format(requireContext().resources.getString(R.string.total_athletes), "$total")
    }

    companion object {
        const val ATHLETES_REFRESH_KEY = "athletes_resume_key"
    }
}