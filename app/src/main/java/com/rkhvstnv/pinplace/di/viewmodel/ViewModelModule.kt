package com.rkhvstnv.pinplace.di.viewmodel

import androidx.lifecycle.ViewModel
import com.rkhvstnv.pinplace.ui.addplace.AddPlaceViewModel
import com.rkhvstnv.pinplace.ui.allplaces.AllPlacesViewModel
import com.rkhvstnv.pinplace.ui.placedetails.PlaceDetailsFragment
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
}