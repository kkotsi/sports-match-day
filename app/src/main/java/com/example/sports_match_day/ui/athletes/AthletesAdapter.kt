package com.example.sports_match_day.ui.athletes

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
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.utils.FlagManager
import com.example.sports_match_day.utils.constants.PreferencesKeys
import com.pixplicity.easyprefs.library.Prefs
import com.squareup.picasso.Picasso

/**
 * Created by Kristo on 10-Mar-21
 */
class AthletesAdapter(private val onSelect: (Athlete) -> Unit, private val searchCityInMap: (String) -> Unit):
PagingDataAdapter<Athlete, AthletesAdapter.MyViewHolder>(diff()) {

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_name)
        private val textCity: TextView = view.findViewById(R.id.text_city)
        private val textMatchesParticipated: TextView = view.findViewById(R.id.text_matches_participated)
        private val textSport: TextView = view.findViewById(R.id.text_sport)
        private val textBirthday: TextView = view.findViewById(R.id.text_birthday)
        private val imageCountry: ImageView = view.findViewById(R.id.image_country)
        private val imageGender: ImageView = view.findViewById(R.id.image_gender)
        private val imagePerson: ImageView = view.findViewById(R.id.image_person)

        fun bind(item: Athlete) {
            textName.text = item.name

            view.findViewById<View>(R.id.container_athlete)?.setOnClickListener {
                onSelect(item)
            }

            if(item.gender == Gender.MALE){
                imageGender.setImageResource(R.drawable.ic_male)
                imagePerson.setImageResource(R.drawable.male)
            }else{
                imageGender.setImageResource(R.drawable.ic_female)
                imagePerson.setImageResource(R.drawable.female)
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
            textSport.text = item.sport?.name
            textBirthday.text = item.birthday.toString().substring(0, 10)

            val debugMatches = item.matches.toString()
            textMatchesParticipated.text = debugMatches
            textMatchesParticipated.isVisible = Prefs.getBoolean(PreferencesKeys.DEBUG_ON, false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_athlete, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    fun getAthlete(position: Int): Athlete? {
        return getItem(position)
    }
}

private fun diff() : DiffUtil.ItemCallback<Athlete>{
    return object : DiffUtil.ItemCallback<Athlete>(){
        override fun areItemsTheSame(oldItem: Athlete, newItem: Athlete): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Athlete, newItem: Athlete): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.gender == newItem.gender
        }
    }
}