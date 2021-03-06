package com.example.sports_match_day.ui.squads

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.utils.FlagManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.google.android.gms.maps.model.LatLng
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.picasso.Picasso

/**
 * Created by Kristo on 10-Mar-21
 */
class SquadsAdapter(
    private val onSelect: (Squad) -> Unit,
    private val searchStadiumInMap: (String, LatLng?) -> Unit,
    private val searchCityInMap: (String) -> Unit
) :
    PagingDataAdapter<Squad, SquadsAdapter.MyViewHolder>(diff()) {

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_squad)
        private val textMatchesParticipated: TextView = view.findViewById(R.id.text_matches_participated)
        private val textCity: TextView = view.findViewById(R.id.text_city)
        private val textStadium: TextView = view.findViewById(R.id.text_stadium)
        private val textSport: TextView = view.findViewById(R.id.text_sport)
        private val textBirthday: TextView = view.findViewById(R.id.text_birthday)
        private val imageCountry: ImageView = view.findViewById(R.id.image_country)

        fun bind(item: Squad) {
            textName.text = item.name

            view.findViewById<View>(R.id.container_squad)?.setOnClickListener {
                onSelect(item)
            }

            val url = FlagManager.getFlagURL(item.getCountryCode())

            Picasso
                .with(view.context)
                .load(url)
                .into(imageCountry)

            textCity.text = item.city
            textCity.setOnClickListener {
                searchCityInMap(item.city)
            }
            textStadium.text = item.stadium
            textStadium.setOnClickListener {
                searchStadiumInMap(item.stadium, item.stadiumLocation)
            }
            textSport.text = item.sport?.name ?: ""
            textBirthday.text = item.birthday.year.toString()

            val debugMatches = item.matches.toString()
            textMatchesParticipated.text = debugMatches
            textMatchesParticipated.isVisible = Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
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


private fun diff(): DiffUtil.ItemCallback<Squad> {
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