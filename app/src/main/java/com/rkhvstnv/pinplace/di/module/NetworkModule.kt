/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.di.module

import com.rkhvstnv.pinplace.api.WeatherApi
import com.rkhvstnv.pinplace.api.WeatherService
import com.rkhvstnv.pinplace.utils.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
class NetworkModule {
    @Singleton
    @Provides
    fun providesWeatherApi(): WeatherApi{
        return Retrofit
            .Builder()
            .baseUrl(Constants.WEATHER_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    @Singleton
    @Provides
    fun providesWeatherService(api: WeatherApi): WeatherService{
        return  WeatherService(api = api)
    }
}