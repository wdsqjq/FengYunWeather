package me.wsj.fengyun.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.wsj.fengyun.databinding.ItemTopCityBinding

class TopCityAdapter(val mData: List<String>, val onChecked: (String) -> Unit) :
    RecyclerView.Adapter<TopCityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(
//            LayoutInflater.from(parent.context).inflate(R.layout.item_top_city, parent, false)
            ItemTopCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.binding.tvCityName.text = item

        holder.itemView.setOnClickListener {
            onChecked(item)
        }
    }

    override fun getItemCount() = mData.size

    class ViewHolder(val binding: ItemTopCityBinding) : RecyclerView.ViewHolder(binding.root) {
//        val tvCityName = itemView.findViewById<TextView>(R.id.tvCityName)
    }
}