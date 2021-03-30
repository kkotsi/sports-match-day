package com.example.sports_match_day.ui.squads

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.databinding.FragmentSquadsBinding
import com.example.sports_match_day.firebase.ExampleLoadStateAdapter
import com.example.sports_match_day.ui.base.BaseFragment
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsFragment : BaseFragment() {

    private val viewModel: SquadsViewModel by viewModel()
    private lateinit var adapter: SquadsAdapter

    private var _binding: FragmentSquadsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSquadsBinding.inflate(layoutInflater)
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
            navController.navigate(R.id.action_nav_squads_to_nav_squads_add)
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
            viewModel.pagedSquads.collectLatest {
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
                SQUADS_REFRESH_KEY
            )?.observe(
                viewLifecycleOwner
            ) {
                adapter.refresh()
            }
        }
    }

    private fun editSquad(squadId: Int) {
        val navController =
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val action = SquadsFragmentDirections.actionNavSquadsToNavSquadsAdd(squadId)
        navController.navigate(action)
    }

    private fun searchCity(city: String) {
        viewModel.getCity(requireContext(),city){
            val gmmIntentUri = if(it != null) {
                 Uri.parse("geo:${it.latitude},${it.longitude}")
            }else{
                Uri.parse("geo:0,0?q=$city")
            }
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }
    }

    private fun searchStadium(stadium: String) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=$stadium stadium")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    private fun recyclerSetup() {
        view?.let {
            binding.recyclerSquads.layoutManager = LinearLayoutManager(requireContext())

            adapter = SquadsAdapter({ squad ->
                editSquad(squad.id)
            }, { searchStadium(it) }) {
                searchCity(it)
            }

            binding.recyclerSquads.adapter = adapter.withLoadStateHeaderAndFooter(
                header = ExampleLoadStateAdapter { adapter.refresh() },
                footer = ExampleLoadStateAdapter { adapter.refresh() }
            )

            binding.recyclerSquads.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                    viewModel.removeSquad(adapter.getSquad(position))
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(binding.recyclerSquads)
        }
    }

    private fun refreshCount() {
        val total = adapter.itemCount
        binding.textSquadsTotal.text =
            String.format(requireContext().resources.getString(R.string.total_squads), "$total")
    }

    companion object {
        const val SQUADS_REFRESH_KEY = "squads_resume_key"
    }
}