package com.example.sports_match_day.ui.sports

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Sport
import com.example.sports_match_day.model.SportType

/**
 * Created by Kristo on 11-Mar-21
 */
class SportsAdapter(private val onSelect: (Sport) -> Unit):
    PagingDataAdapter<Sport, SportsAdapter.MyViewHolder>(diff()) {

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_sport)
        private val imageGender: ImageView = view.findViewById(R.id.image_sport_gender)
        private val imageSportType: ImageView = view.findViewById(R.id.image_sport_type)

        fun bind(item: Sport) {
            textName.text = item.name

            view.findViewById<View>(R.id.container_sport)?.setOnClickListener {
                onSelect(item)
            }

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

private fun diff() : DiffUtil.ItemCallback<Sport>{
    return object : DiffUtil.ItemCallback<Sport>(){
        override fun areItemsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Sport, newItem: Sport): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.type == newItem.type &&
                    oldItem.gender == newItem.gender
        }
    }
}