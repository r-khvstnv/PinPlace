/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.api

import com.rkhvstnv.pinplace.model.WeatherModel
import com.rkhvstnv.pinplace.utils.Constants
import com.rkhvstnv.pinplace.keys.Keys
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class WeatherService @Inject constructor(private val api: WeatherApi) {
    fun getWeather(lat: Double, lon: Double): Single<WeatherModel>{
        return  api.getWeatherData(
            lat = lat,
            lon = lon,
            Constants.WEATHER_API_UNITS_VALUE,
            Constants.WEATHER_API_EXCLUDED_VALUE,
            Keys.WEATHER_API_KEY_VALUE
        )
    }
}