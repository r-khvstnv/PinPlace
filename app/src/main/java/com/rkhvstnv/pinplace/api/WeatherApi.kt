package com.rkhvstnv.pinplace.api

import com.rkhvstnv.pinplace.model.WeatherModel
import com.rkhvstnv.pinplace.utils.Constants
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET(Constants.WEATHER_API_ENDPOINT)
    fun getWeatherData(
        @Query(Constants.WEATHER_API_LAT) lat: Double,
        @Query(Constants.WEATHER_API_LON) lon: Double,
        @Query(Constants.WEATHER_API_UNITS) units: String,
        @Query(Constants.WEATHER_API_EXCLUDE) exclude: String,
        @Query(Constants.WEATHER_API_ID) id: String
    ): Single<WeatherModel>
}