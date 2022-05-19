package com.rkhvstnv.pinplace.di

import android.app.Application
import com.rkhvstnv.pinplace.di.component.AppComponent
import com.rkhvstnv.pinplace.di.component.DaggerAppComponent

class PinPlaceApplication: Application() {
    lateinit var appComponent: AppComponent private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)
    }
}