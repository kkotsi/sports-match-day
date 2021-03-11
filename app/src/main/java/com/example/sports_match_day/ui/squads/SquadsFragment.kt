package com.example.sports_match_day.ui.squads

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Created by Kristo on 05-Mar-21
 */
class SquadsFragment : Fragment() {

    private val viewModel: SquadsViewModel by viewModel()
    private lateinit var recyclerSquads: RecyclerView
    private lateinit var textTotal: TextView
    private lateinit var loader: ProgressBar
    private var total = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_squads, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoader()
        setupTotalText()
        recyclerSetup()
        setupObservers()
    }

    private fun setupLoader(){
        view?.findViewById<ProgressBar>(R.id.progress_loading)?.let{
            loader = it
        }
    }

    private fun setupTotalText(){
        view?.findViewById<TextView>(R.id.text_squads_total)?.let{
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

        viewModel.pagedSquads.observe(viewLifecycleOwner, {
            it.addWeakCallback(null, object: PagedList.Callback() {
                override fun onChanged(position: Int, count: Int) {
                    total = count
                    textTotal.text =  String.format(requireContext().resources.getString(R.string.total_squads), "$total")
                }
                override fun onInserted(position: Int, count: Int) {
                    total += count
                    textTotal.text = String.format(requireContext().resources.getString(R.string.total_squads), "$total")
                    loader.visibility = View.INVISIBLE
                }
                override fun onRemoved(position: Int, count: Int) {
                    total -= count
                    textTotal.text = String.format(requireContext().resources.getString(R.string.total_squads), "$total")
                }
            })
            (recyclerSquads.adapter as SquadsAdapter).submitList(it)
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
                    val position = viewHolder.adapterPosition
                    viewModel.removeSquad((recyclerSquads.adapter as? SquadsAdapter)?.getSquad(position))
                    recyclerSquads.adapter?.notifyItemRemoved(position)
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
            itemTouchHelper.attachToRecyclerView(recyclerSquads)
        }
    }
}