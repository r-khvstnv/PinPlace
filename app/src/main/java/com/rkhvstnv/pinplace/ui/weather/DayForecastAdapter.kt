/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.ItemDayForecastBinding
import com.rkhvstnv.pinplace.model.Daily
import com.rkhvstnv.pinplace.utils.WeatherUtils
import com.rkhvstnv.pinplace.utils.loadIntImage

class DayForecastAdapter(private val context: Context
): RecyclerView.Adapter<DayForecastAdapter.ViewHolder>() {

    private var dayList: List<Daily> = listOf()

    inner class ViewHolder(val binding: ItemDayForecastBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DayForecastAdapter.ViewHolder {
        val bind = ItemDayForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(bind)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dayList[position]
        val cSign = context.getString(R.string.st_celsius_sign)

        with(holder.binding){
            ivWeatherImageDf.loadIntImage(WeatherUtils.setRightImage(item.weather[0].id))

            tvDayDf.text = WeatherUtils.getDayName(item.dt.toLong())
            tvWeatherTitleDf.text = item.weather[0].main
            tvTemperatureDf.text =
                item.temp.max.toInt().toString() + cSign + "/" +
                        item.temp.min.toInt().toString() + cSign
        }
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Daily>){
        dayList = list
        notifyDataSetChanged()
    }
}