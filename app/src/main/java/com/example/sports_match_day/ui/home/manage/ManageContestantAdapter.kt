package com.example.sports_match_day.ui.home.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Sport
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Kristo on 25-Mar-21
 */
class AddContestantViewHolder(
    view: View,
    addContestant: () -> Unit
) : RecyclerView.ViewHolder(view) {
    private val retry: FloatingActionButton = view.findViewById<FloatingActionButton>(R.id.fab_add)
        .also {
            it.setOnClickListener { addContestant() }
        }

    fun bind() {

    }
}

class AddContestantAdapter(
    var show: Boolean,
    private val addContestant: () -> Unit
) :
    RecyclerView.Adapter<AddContestantViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddContestantViewHolder {
        return AddContestantViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_contestant, parent, false),
            addContestant
        )
    }

    override fun onBindViewHolder(holder: AddContestantViewHolder, position: Int) {
        holder.bind()
    }

    fun refresh(size: Int, sport: Sport?){
        val state = show
        show = (sport?.participantCount ?: size + 1) > size

        if(state != show)
            notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if(show) 1 else 0
    }
}