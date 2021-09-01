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

class SwipeAdapter(
    val context: Context,
    val mData: List<WeatherBean>,
    val onClick: (WeatherBean) -> Unit,
    val onDelete: (Int) -> Unit
) :
    RecyclerView.Adapter<SwipeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_swipe, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.tvWeather.text = item.name
        holder.icon.setImageResource(IconUtils.getDayIconDark(context, item.code.toString()))
        holder.itemContent.setOnClickListener {
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
        val itemContent = itemView.findViewById<ViewGroup>(R.id.itemContent)
        val icon = itemView.findViewById<ImageView>(R.id.icon)
        val tvDelete = itemView.findViewById<TextView>(R.id.tvDelete)
    }
}