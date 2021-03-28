package com.example.sports_match_day.ui.squads.manage

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.sports_match_day.R

/**
 * Created by Kristo on 28-Mar-21
 */
class DateAdapter (var context: Context, private var layoutId: Int, private var dates: List<String>): BaseAdapter() {

    private class ViewHolder(view: View) {
        var textName: TextView = view.findViewById(R.id.textView)

        fun bind(date: String){
            textName.text = date
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if(view == null) {
            view = LayoutInflater.from(context).inflate(layoutId, parent, false)

            val vh = ViewHolder(view)
            vh.bind(dates[position])
            view.tag = vh
        }else{
            (view.tag as ViewHolder).bind(dates[position])
        }

        return view!!
    }

    override fun getItem(position: Int): Any {
        return dates[position]
    }

    override fun getItemId(position: Int): Long {
        return dates[position].hashCode().toLong()
    }

    override fun getCount(): Int {
        return dates.size
    }

    fun getItemPosition(item: String): Int {
        return dates.indexOf(item)
    }
}