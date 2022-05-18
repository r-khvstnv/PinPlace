package com.rkhvstnv.pinplace.ui.weather

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.databinding.ItemHourForecastBinding
import com.rkhvstnv.pinplace.model.Hourly
import com.rkhvstnv.pinplace.utils.WeatherUtils
import com.rkhvstnv.pinplace.utils.loadIntImage

class HourForecastAdapter(private val context: Context
): RecyclerView.Adapter<HourForecastAdapter.ViewHolder>() {

    private var hourlyList: List<Hourly> = listOf()

    inner class ViewHolder(val binding: ItemHourForecastBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val bind = ItemHourForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(bind)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = hourlyList[position]

        with(holder.binding){
            tvTemperatureHf.text =
                item.temp.toInt().toString() + context.getString(R.string.st_celsius_sign)
            ivWeatherImageHf.loadIntImage(WeatherUtils.setRightImage(item.weather[0].id))
            tvWindSpeedHf.text =
                WeatherUtils.getRoundedDouble(item.wind_speed) + context.getString(R.string.st_km_per_hour)

            val time = WeatherUtils.getUnixTime(item.dt.toLong())

            if (time[0].toString().toInt() == 0 && time[1].toString().toInt() == 0){
                tvDayHf.visibility = View.VISIBLE
                tvDayHf.text = WeatherUtils.getDayName(item.dt.toLong())
            } else{
                tvDayHf.visibility = View.GONE
            }
            tvTimeHf.text = time
        }
    }

    override fun getItemCount(): Int {
        return hourlyList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<Hourly>){
        hourlyList = list
        notifyDataSetChanged()
    }

}