package com.rkhvstnv.pinplace.di.app

import android.app.Application
import com.rkhvstnv.pinplace.ui.weather.WeatherFragment
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(application: Application)

    fun inject(fragment: WeatherFragment)
}