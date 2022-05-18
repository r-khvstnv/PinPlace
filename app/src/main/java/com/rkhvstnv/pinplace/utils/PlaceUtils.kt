package com.rkhvstnv.pinplace.utils

import android.content.Context
import android.location.Geocoder
import java.text.SimpleDateFormat
import java.util.*

object PlaceUtils {

    fun formatToDate(time: Long): String{
        val date = Date(time)
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

    fun getFullAddressFromLocation(
        context: Context,
        lat: Double,
        lon: Double
    ): String{
        var result = ""
        val geo = Geocoder(context, Locale.getDefault())
        val location = geo.getFromLocation(lat, lon, 1)

        result = location[0].getAddressLine(0)

        return result
    }
}