package me.wsj.fengyun.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.wsj.fengyun.R
import me.wsj.fengyun.bean.Daily
import me.wsj.fengyun.databinding.ItemForecast15Binding
import me.wsj.fengyun.view.TempChart
import me.wsj.lib.utils.IconUtils
import java.util.*

class Forecast15dAdapter(val context: Context, val datas: List<Daily>) :
    RecyclerView.Adapter<Forecast15dAdapter.ViewHolder>() {

    private var mMin = 0
    private var mMax = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemForecast15Binding.inflate(LayoutInflater.from(context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datas[position]
        holder.binding.tvWeek.text = getWeekDay(position)
        holder.binding.tvDate.text = item.fxDate.removeRange(IntRange(0, 4))
        holder.binding.tvDayDesc.text = item.textDay
//        val dayIcon = IconUtils.pluginRes(context).getDrawable(IconUtils.getDayIconDark(context, item.iconDay))
//        holder.binding.ivDay.setImageResource(IconUtils.getDayIconDark(context, item.iconDay))
        holder.binding.ivDay.setImageDrawable(IconUtils.getDayIcon(context, item.iconDay))

//        holder.binding.ivNight.setImageResource(IconUtils.getNightIconDark(context, item.iconNight))
        holder.binding.ivNight.setImageDrawable(IconUtils.getNightIcon(context, item.iconDay))
        holder.binding.tvNightDesc.text = item.textNight
        holder.binding.tvWind.text = item.windDirDay
        holder.binding.tvWindScale.text = item.windScaleDay + "级"

        holder.binding.tempChart.setData(
            mMin,
            mMax,
            if (position == 0) null else datas[position - 1],
            item,
            if (position == datas.size - 1) null else datas[position + 1]
        )
    }

    val weeks = arrayOf("周日", "周一", "周二", "周三", "周四", "周五", "周六")

    private fun getWeekDay(position: Int): String {
        if (position == 0) {
            return "今天"
        } else {
            val calendar = Calendar.getInstance()
            val dateArray = datas[position].fxDate.split("-")
            calendar.set(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
            var w = calendar.get(Calendar.DAY_OF_WEEK) - 1
            if (w < 0) {
                w = 0
            }
            return weeks[w]
        }
    }

    override fun getItemCount(): Int = datas.size

    fun setRange(min: Int, max: Int) {
        mMin = min
        mMax = max
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemForecast15Binding) : RecyclerView.ViewHolder(binding.root) {
    }
}