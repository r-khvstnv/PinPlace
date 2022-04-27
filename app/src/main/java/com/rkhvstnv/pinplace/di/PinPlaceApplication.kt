package com.rkhvstnv.pinplace.di

import android.app.Application
import android.content.Context
import com.rkhvstnv.pinplace.di.app.AppComponent
import com.rkhvstnv.pinplace.di.app.DaggerAppComponent

class PinPlaceApplication: Application() {
    lateinit var appComponent: AppComponent private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.create()
        appComponent.inject(this)
    }
}