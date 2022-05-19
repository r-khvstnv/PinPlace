package com.rkhvstnv.pinplace.di

import android.app.Application
import com.rkhvstnv.pinplace.di.component.AppComponent
import com.rkhvstnv.pinplace.di.component.DaggerAppComponent
import com.rkhvstnv.pinplace.di.module.DatabaseModule

class PinPlaceApplication: Application() {
    lateinit var appComponent: AppComponent private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().databaseModule(DatabaseModule(this)).build()
        appComponent.inject(this)
    }
}