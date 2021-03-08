package com.example.sports_match_day.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Match
import com.example.sports_match_day.model.SportType
import com.example.sports_match_day.utils.FlagManager
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import org.threeten.bp.format.TextStyle
import java.util.*


/**
 * Created by Kristo on 07-Mar-21
 */
class MatchAdapter(private val context: Context, private val matches: List<Match>) :
    RecyclerView.Adapter<MatchAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return matches.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val countryImage: ImageView = itemView.findViewById<ImageView>(R.id.image_country)
        val textSport: TextView = view.findViewById<TextView>(R.id.text_sport)
        val participantFirst: TextView = view.findViewById<TextView>(R.id.text_first_squad)
        val participantSecond: TextView = view.findViewById<TextView>(R.id.text_sec_squad)
        val scoreFirst: TextView = view.findViewById<TextView>(R.id.text_first_score)
        val scoreSecond: TextView = view.findViewById<TextView>(R.id.text_sec_score)
        val date: TextView = view.findViewById<TextView>(R.id.text_date)
        val moreText: TextView = view.findViewById<TextView>(R.id.text_more)
        val genderImage : ImageView = itemView.findViewById<ImageView>(R.id.image_sport_gender)
        val typeImage: ImageView = itemView.findViewById<ImageView>(R.id.image_sport_type)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val holder =
            MyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
            )

        holder.apply {
            itemView.findViewById<View>(R.id.container_match)?.setOnClickListener {
                if (adapterPosition >= 0) {

                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.apply {
            val match = matches[position]
            val country = match.country.toString()
            val url = FlagManager.getFlagURL(country)

            textSport.text = match.sport.name
            participantFirst.text = match.participants[0].id.toString()
            participantSecond.text = match.participants[1].id.toString()

            scoreFirst.text = match.participants[0].score.toString()
            scoreSecond.text = match.participants[1].score.toString()

            val dateString = match.date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " " + match.date.dayOfMonth + "/" + match.date.monthValue + "\n" + match.date.hour + ":" + match.date.minute
            date.text = dateString

            if(match.sport.gender == Gender.MALE){
                genderImage.setImageResource(R.drawable.ic_male)
            }else{
                genderImage.setImageResource(R.drawable.ic_female)
            }

            if(match.sport.type == SportType.SOLO){
                typeImage.setImageResource(R.drawable.ic_person)
            }else{
                typeImage.setImageResource(R.drawable.ic_group)

                if(match.participants.size > 2)
                    moreText.visibility = View.VISIBLE
                else
                    moreText.visibility = View.GONE
            }

            Picasso
                .with(context)
                .load(url)
                .into(countryImage)
        }
    }
}