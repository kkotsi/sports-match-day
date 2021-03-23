package com.example.sports_match_day.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.model.Squad
import com.example.sports_match_day.utils.FlagManager
import com.squareup.picasso.Picasso
import org.threeten.bp.format.TextStyle
import java.text.DecimalFormat
import java.util.*


/**
 * Created by Kristo on 07-Mar-21
 */
class MatchAdapter(
    private val selectMatch: (Match) -> Unit
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
        private val moreText: TextView = view.findViewById(R.id.text_more)
        private val locationText: TextView = view.findViewById(R.id.text_location)
        private val genderImage: ImageView = view.findViewById(R.id.image_sport_gender)
        private val typeImage: ImageView = view.findViewById(R.id.image_sport_type)
        private val extraLayout: LinearLayout = view.findViewById(R.id.extra)

        fun bind(match: Match) {
            val country = match.country.toString()
            val url = FlagManager.getFlagURL(country)

            view.findViewById<View>(R.id.container_match)?.setOnClickListener {
                selectMatch.invoke(match)
                lastSelected?.let {
                    it.extraLayout.visibility = View.GONE
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

                participantFirst.text = match.participants[0].participant?.name ?: ""
                val score = match.participants[0].score

                if (score >= 0)
                    scoreFirst.text = format.format(match.participants[0].score).toString()
                else
                    scoreFirst.text = "-"

                participantSecond.text = match.participants[1].participant?.name ?: ""

                val secondScore = match.participants[1].score
                if (secondScore >= 0)
                    scoreSecond.text = format.format(match.participants[1].score).toString()
                else
                    scoreSecond.text = "-"
            }

            val dateString = match.date.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            ) + " " + match.date.dayOfMonth + "/" + match.date.monthValue + "\n" + match.date.hour + ":" + match.date.minute
            date.text = dateString

            var location = match.city

            match.sport?.let {
                if (it.gender == Gender.MALE) {
                    genderImage.setImageResource(R.drawable.ic_male)
                } else {
                    genderImage.setImageResource(R.drawable.ic_female)
                }

                if (it.type == SportType.SOLO) {
                    typeImage.setImageResource(R.drawable.ic_person)
                } else {
                    location += " \n" + itemView.context.resources.getString(R.string.stadium) + ": " + (match.participants[0].participant as? Squad)?.stadium
                    typeImage.setImageResource(R.drawable.ic_group)
                }
            }

            if (match.participants.size > 2)
                moreText.visibility = View.VISIBLE
            else
                moreText.visibility = View.GONE

            locationText.text = location
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