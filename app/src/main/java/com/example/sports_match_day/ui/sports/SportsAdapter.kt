package com.example.sports_match_day.ui.sports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.SportType

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsAdapter:
    PagedListAdapter<Sport, SportsAdapter.MyViewHolder>(
        diff()
    ) {

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById<TextView>(R.id.text_sport)
        private val imageGender: ImageView = view.findViewById<ImageView>(R.id.image_sport_gender)
        private val imageSportType: ImageView = view.findViewById<ImageView>(R.id.image_sport_type)

        fun bind(item: Sport) {
            textName.text = item.name

            if(item.gender == Gender.MALE){
                imageGender.setImageResource(R.drawable.ic_male)
            }else{
                imageGender.setImageResource(R.drawable.ic_female)
            }

            if(item.type == SportType.TEAM){
                imageSportType.setImageResource(R.drawable.ic_group)
            }else{
                imageSportType.setImageResource(R.drawable.ic_person)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_sport, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    fun getSport(position: Int): Sport? {
        return getItem(position)
    }
}

fun diff() : DiffUtil.ItemCallback<Sport>{
    return object : DiffUtil.ItemCallback<Sport>(){
        override fun areItemsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return oldItem.id == newItem.id
        }
    }
}