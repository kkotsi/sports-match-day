package com.example.sports_match_day.ui.dashboard

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sports_match_day.R

/**
 * Created by Kristo on 15-Apr-21
 */
class PieChartColorsAdapter(private var colors: List<Dashboard.PieChartColors>):
    RecyclerView.Adapter<PieChartColorsAdapter.MyViewHolder>() {

    class MyViewHolder(var context: Context, val view: View) : RecyclerView.ViewHolder(view){
        private val imageColor = view.findViewById<ImageView>(R.id.image_color)
        private val imageName = view.findViewById<TextView>(R.id.text_color_name)
        fun bind(item: Dashboard.PieChartColors){
            ImageViewCompat.setImageTintList(imageColor, ColorStateList.valueOf(item.color))
            imageName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder(
            parent.context,
            LayoutInflater.from(parent.context).inflate(R.layout.item_pie_chart_color, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(colors[position])
    }

    override fun getItemCount() = colors.size

}