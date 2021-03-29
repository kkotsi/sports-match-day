package com.example.sports_match_day.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.utils.FlagManager
import com.squareup.picasso.Picasso
import org.threeten.bp.format.DateTimeFormatter
import java.text.DecimalFormat


/**
 * Created by Kristo on 07-Mar-21
 */
class MatchAdapter(
    private val selectMatch: (Match) -> Unit,
    private val searchStadiumInMap: (String) -> Unit
) : PagingDataAdapter<Match, MatchAdapter.MyViewHolder>(diff()) {

    private var lastSelected: MyViewHolder? = null

    inner class MyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val countryImage: ImageView = view.findViewById(R.id.image_country)
        private val textSport: TextView = view.findViewById(R.id.text_sport)
        private val participantFirst: TextView = view.findViewById(R.id.text_first_squad)
        private val participantSecond: TextView = view.findViewById(R.id.text_sec_squad)
        private val scoreFirst: TextView = view.findViewById(R.id.text_first_score)
        private val scoreSecond: TextView = view.findViewById(R.id.text_sec_score)
        private val date: TextView = view.findViewById(R.id.text_date)
        private val moreImage: ImageView = view.findViewById(R.id.image_more)
        private val locationText: TextView = view.findViewById(R.id.text_location)
        private val genderImage: ImageView = view.findViewById(R.id.image_sport_gender)
        private val typeImage: ImageView = view.findViewById(R.id.image_sport_type)
        private val extraLayout: LinearLayout = view.findViewById(R.id.extra)

        fun bind(match: Match) {
            val country = match.getCountryCode()
            val url = FlagManager.getFlagURL(country)

            view.findViewById<View>(R.id.container_match)?.setOnClickListener {
                selectMatch.invoke(match)
                lastSelected?.let {
                    it.extraLayout.visibility = View.INVISIBLE
                }
                lastSelected = this

                lastSelected?.let {
                    it.extraLayout.visibility = View.VISIBLE
                }
            }

            Picasso
                .with(view.context)
                .load(url)
                .into(countryImage)

            val format = DecimalFormat("0.#")

            textSport.text = match.sport?.name ?: ""

            if (match.participants.size > 1) {

                participantFirst.text = match.participants[0].contestant?.name ?: ""
                val score = match.participants[0].score

                if (score >= 0)
                    scoreFirst.text = format.format(match.participants[0].score).toString()
                else
                    scoreFirst.text = "-"

                participantSecond.text = match.participants[1].contestant?.name ?: ""

                val secondScore = match.participants[1].score
                if (secondScore >= 0)
                    scoreSecond.text = format.format(match.participants[1].score).toString()
                else
                    scoreSecond.text = "-"
            }

            val formatter = DateTimeFormatter.ofPattern("dd/MMM/yy\nhh:mm")
            date.text = match.date.format(formatter)

            match.sport?.let {
                if (it.gender == Gender.MALE) {
                    genderImage.setImageResource(R.drawable.ic_male)
                } else {
                    genderImage.setImageResource(R.drawable.ic_female)
                }
                if (it.type == SportType.SOLO) {
                    typeImage.setImageResource(R.drawable.ic_person)
                } else {
                    typeImage.setImageResource(R.drawable.ic_group)
                }
            }

            moreImage.isVisible = match.participants.size > 2

            val location = "${match.city}, ${match.stadium}"
            locationText.text = location
            locationText.setOnClickListener {
                searchStadiumInMap(match.stadium)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    fun getMatch(position: Int): Match? {
        return getItem(position)
    }
}

fun diff(): DiffUtil.ItemCallback<Match> {
    return object : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(oldItem: Match, newItem: Match): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Match, newItem: Match): Boolean {
            return oldItem.id == newItem.id
        }
    }
}