package com.example.sports_match_day.ui.athletes.add

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.sports_match_day.R
import com.example.sports_match_day.model.Sport


/**
 * Created by Kristo on 14-Mar-21
 */
class SportsAdapter(var context: Context, var sports: List<Sport>): BaseAdapter() {

    private class ViewHolder(view: View) {
        var textName: TextView = view.findViewById(R.id.textView)

        fun bind(sport: Sport){
            textName.text = sport.name
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_spinner, parent, false)

            val vh = ViewHolder(view)
            vh.bind(sports[position])
            view.tag = vh
        }else{
            (view.tag as ViewHolder).bind(sports[position])
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return sports[position]
    }

    override fun getItemId(position: Int): Long {
        return sports[position].id.toLong()
    }

    override fun getCount(): Int {
        return sports.size
    }
}