package com.example.sports_match_day.ui.squads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.utils.FlagManager
import com.squareup.picasso.Picasso

/**
 * Created by Kristo on 10-Mar-21
 */
class SquadsAdapter :
    PagingDataAdapter<Squad, SquadsAdapter.MyViewHolder>(diff()) {

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_squad)
        private val textCity: TextView = view.findViewById(R.id.text_city)
        private val textStadium: TextView = view.findViewById(R.id.text_stadium)
        private val textSport: TextView = view.findViewById(R.id.text_sport)
        private val textBirthday: TextView = view.findViewById(R.id.text_birthday)
        private val imageCountry: ImageView = view.findViewById(R.id.image_country)

        fun bind(item: Squad) {
            textName.text = item.name

            val url = FlagManager.getFlagURL(item.country.toString())

            Picasso
                .with(view.context)
                .load(url)
                .into(imageCountry)

            textCity.text = item.city?.locality ?: item.city?.adminArea ?: item.city?.countryName
            textStadium.text = item.stadium
            textSport.text = item.sport?.name ?: ""
            textBirthday.text = item.birthday.year.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_squad, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    fun getSquad(position: Int): Squad? {
        return getItem(position)
    }
}


fun diff(): DiffUtil.ItemCallback<Squad> {
    return object : DiffUtil.ItemCallback<Squad>() {
        override fun areItemsTheSame(oldItem: Squad, newItem: Squad): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Squad, newItem: Squad): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.birthday == newItem.birthday
        }
    }
}