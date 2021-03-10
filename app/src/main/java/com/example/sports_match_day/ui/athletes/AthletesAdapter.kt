package com.example.sports_match_day.ui.athletes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Athlete

/**
 * Created by Kristo on 10-Mar-21
 */
class AthletesAdapter(
    private val context: Context
    ) :
    PagedListAdapter<Athlete, AthletesAdapter.MyViewHolder>(
        diff()
    ) {

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textName: TextView = view.findViewById<TextView>(R.id.text_name)
        fun bind(item: Athlete) {
            textName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder =
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_athlete, parent, false)
            )
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCurrentListChanged(
        previousList: PagedList<Athlete>?,
        currentList: PagedList<Athlete>?
    ) {
        super.onCurrentListChanged(previousList, currentList)
        print("changed")
    }
}

fun diff() : DiffUtil.ItemCallback<Athlete>{
    return object : DiffUtil.ItemCallback<Athlete>(){
        override fun areItemsTheSame(oldItem: Athlete, newItem: Athlete): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Athlete, newItem: Athlete): Boolean {
            return oldItem.id == newItem.id
        }
    }
}