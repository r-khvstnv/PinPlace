package com.rkhvstnv.pinplace.utils

import androidx.annotation.DrawableRes
import com.rkhvstnv.pinplace.R
import com.rkhvstnv.pinplace.model.SunriseModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

object WeatherUtils {
    @DrawableRes
    fun setRightImage(id: Int): Int{
        return when(id){
            in 200..232 -> R.drawable.ic_thunderstorm
            in 300..321 -> R.drawable.ic_drizzle
            in 500..531 -> R.drawable.ic_showers
            600 -> R.drawable.ic_snow_light
            in 601..602 -> R.drawable.ic_snow
            in 611..622 -> R.drawable.ic_sleet
            in 701..771 -> R.drawable.ic_foggy
            781 -> R.drawable.ic_tornado
            800 -> R.drawable.ic_sunny
            801 -> R.drawable.ic_clear_cloudy
            802 -> R.drawable.ic_partly_cloudy
            803 -> R.drawable.ic_cloudy
            804 -> R.drawable.ic_mostly_cloudy
            else -> 0
        }
    }

    fun getUnixTime(timex: Long): String{
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun getDayName(timex: Long): String{
        val date = Date(timex * 1000L)
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun getRoundedDouble(x: Double): String{
        val df = DecimalFormat("#.#")
        return  df.format(x)
    }

    fun getSunriseProgress(_sunrise: Long, _sunset: Long): SunriseModel {
        val sunrise = getUnixTime(_sunrise)
        val sunset = getUnixTime(_sunset)
        val curTime = System.currentTimeMillis()

        val max: Int = ((_sunset - _sunrise) / 3600L).toInt()
        val progress: Int = (((curTime / 1000L) - _sunrise) / 3600L).toInt()

        return SunriseModel(sunrise = sunrise, sunset = sunset, progress, max)
    }
}