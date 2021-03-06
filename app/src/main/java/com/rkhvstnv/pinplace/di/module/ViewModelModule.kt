/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.di.module

import androidx.lifecycle.ViewModel
import com.rkhvstnv.pinplace.di.factory.ViewModelKey
import com.rkhvstnv.pinplace.ui.addplace.AddPlaceViewModel
import com.rkhvstnv.pinplace.ui.allplaces.AllPlacesViewModel
import com.rkhvstnv.pinplace.ui.map.MapViewModel
import com.rkhvstnv.pinplace.ui.placedetails.PlaceDetailsViewModel
import com.rkhvstnv.pinplace.ui.weather.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(WeatherViewModel::class)]
    abstract fun bindWeatherViewModel(viewModel: WeatherViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(AddPlaceViewModel::class)]
    abstract fun bindAddPlaceViewModel(viewModel: AddPlaceViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(AllPlacesViewModel::class)]
    abstract fun bindAllPlacesViewModel(viewModel: AllPlacesViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(PlaceDetailsViewModel::class)]
    abstract fun bindPlaceDetailsViewModel(viewModel: PlaceDetailsViewModel): ViewModel

    @Binds
    @[IntoMap ViewModelKey(MapViewModel::class)]
    abstract fun bindMapViewModel(viewModel: MapViewModel): ViewModel
}