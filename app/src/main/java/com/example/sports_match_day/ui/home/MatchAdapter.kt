package com.example.sports_match_day.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sports_match_day.R
import com.example.sports_match_day.utils.FlagManager
import com.makeramen.roundedimageview.RoundedTransformationBuilder
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation


/**
 * Created by Kristo on 07-Mar-21
 */
class MatchAdapter(private val context: Context, private val languages: List<String>) :
    RecyclerView.Adapter<MatchAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return languages.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

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
            val countryImage = itemView.findViewById<ImageView>(R.id.image_country)

            val country = languages[position]
            val url = FlagManager.getFlagURL(country)

            Picasso
                .with(context)
                .load(url)
                .into(countryImage)
        }
    }
}