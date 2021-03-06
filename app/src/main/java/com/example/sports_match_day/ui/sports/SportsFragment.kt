package com.example.sports_match_day.ui.sports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentSportsBinding
import com.example.sports_match_day.firebase.ExampleLoadStateAdapter
import com.example.sports_match_day.ui.base.BaseFragment
import com.example.sports_match_day.utils.PopupManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsFragment : BaseFragment() {

    private val viewModel: SportsViewModel by viewModel()
    private lateinit var adapter: SportsAdapter

    private var _binding: FragmentSportsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSportsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerSetup()
        setupAddButton()
        setupRefreshLayout()
        setupObservers()
    }

    private fun setupRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }
    }

    private fun setupAddButton() {
        binding.fabAdd.setOnClickListener {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
            navController.navigate(R.id.action_nav_sports_to_nav_sports_add)
        }
    }

    private fun setupObservers() {

        viewModel.isDataLoading.observe(viewLifecycleOwner, {
            binding.swipeRefreshLayout.isRefreshing = it
        })

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { loadStates ->
                refreshCount()
                binding.swipeRefreshLayout.isRefreshing = (loadStates.refresh is LoadState.Loading)

                if (loadStates.refresh is LoadState.Error) {
                    showErrorPopup((loadStates.refresh as? LoadState.Error)?.error ?: Throwable())
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.pagedSports.collectLatest {
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
        if (requireActivity().lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
            val navController =
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)

            // A simple way to get data from another fragment with NavController https://developer.android.com/guide/navigation/navigation-programmatic#returning_a_result
            navController.currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
                SPORTS_REFRESH_KEY
            )?.observe(
                viewLifecycleOwner
            ) {
                adapter.refresh()
            }
        }
    }

    private fun editSport(sportId: Int) {
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val action = SportsFragmentDirections.actionNavSportsToNavSportsAdd(sportId)
        navController.navigate(action)
    }

    private fun recyclerSetup() {
        binding.recyclerSports.layoutManager = LinearLayoutManager(requireContext())

        adapter = SportsAdapter { sport ->
            editSport(sport.id)
        }

        binding.recyclerSports.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ExampleLoadStateAdapter { adapter.refresh() },
            footer = ExampleLoadStateAdapter { adapter.refresh() }
        )

        binding.recyclerSports.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    binding.fabAdd.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0 && binding.fabAdd.isShown)
                    binding.fabAdd.hide()
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

                val message = getString(R.string.warning_delete_sports)
                val yes: (Boolean) -> Unit = {
                    val sport = adapter.getSport(position)
                    viewModel.removeSportAndMatches(sport)
                    if(it){
                        viewModel.removeAthletesAndSquadsAndMatches(sport)
                    }
                }
                val no : (Boolean) -> Unit = {
                    val sport = adapter.getSport(position)
                    viewModel.removeSport(sport)
                    if(it){
                        viewModel.removeAthletesAndSquadsAndMatches(sport)
                    }
                }

                PopupManager.deleteSportPopupMessage(requireContext(), message, yes, no){
                    binding.recyclerSports.adapter?.notifyItemChanged(position)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerSports)

    }

    private fun refreshCount() {
        binding.textTotal.isVisible = Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
        val total = adapter.itemCount
        binding.textTotal.text =
            String.format(requireContext().resources.getString(R.string.total_sports), "$total")
    }

    companion object {
        const val SPORTS_REFRESH_KEY = "sports_resume_key"
    }
}