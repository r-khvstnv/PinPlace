package com.rkhvstnv.pinplace.di.viewmodel

import androidx.lifecycle.ViewModel
import com.rkhvstnv.pinplace.ui.weather.WeatherViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @[IntoMap ViewModelKey(WeatherViewModel::class)]
    abstract fun bindWeatherViewModel(viewModel: WeatherViewModel): ViewModel
}