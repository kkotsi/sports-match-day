package com.example.sports_match_day.ui.home.manage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Athlete
import com.example.sports_match_day.model.Gender
import com.example.sports_match_day.model.Participant
import com.example.sports_match_day.model.Squad
import java.text.DecimalFormat

/**
 * Created by Kristo on 24-Mar-21
 */
class ContestantsAdapter(var participants: MutableList<Participant>) :
    RecyclerView.Adapter<ContestantsAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textName: TextView = view.findViewById(R.id.text_name)
        private val imageContestant: ImageView = view.findViewById(R.id.image_contestant)
        private val editTextParticipantScore: EditText =
            view.findViewById(R.id.text_contestant_score)

        fun bind(participant: Participant) {
            val contestant = participant.contestant
            textName.text = contestant?.name

            if (contestant is Squad) {
                imageContestant.setImageResource(R.drawable.team_logo)
            } else if (contestant is Athlete) {
                if (contestant.gender == Gender.MALE)
                    imageContestant.setImageResource(R.drawable.male)
                else
                    imageContestant.setImageResource(R.drawable.female)
            }

            val format = DecimalFormat("0.#")
            editTextParticipantScore.setText(
                format.format(participant.score).toString(),
                TextView.BufferType.EDITABLE
            )
        }
    }

    override fun getItemCount(): Int {
        return participants.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_contestant, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(participants[position])
        holder.itemView.apply {

        }
    }
}