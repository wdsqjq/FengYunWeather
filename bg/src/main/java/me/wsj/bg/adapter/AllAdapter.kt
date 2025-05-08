package me.wsj.bg.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.wsj.bg.R
import me.wsj.bg.bean.WeatherBean
import me.wsj.lib.utils.IconUtils

class AllAdapter(
    val context: Context,
    val mData: List<WeatherBean>,
    val onClick: (WeatherBean) -> Unit,
    val onDelete: (Int) -> Unit
) :
    RecyclerView.Adapter<AllAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_option, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.tvWeather.text = item.name
        holder.icon.setImageResource(IconUtils.getDayIconDark(context, item.code.toString()))
        holder.itemView.setOnClickListener {
            onClick(item)
        }

        holder.tvDelete.setOnClickListener {
            onDelete(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWeather = itemView.findViewById<TextView>(R.id.tvWeather)
        val icon = itemView.findViewById<ImageView>(R.id.icon)
        val tvDelete = itemView.findViewById<TextView>(R.id.tvDelete)
    }
}