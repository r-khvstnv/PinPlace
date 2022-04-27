package com.rkhvstnv.pinplace.utils

import android.content.Context
import com.rkhvstnv.pinplace.di.PinPlaceApplication
import com.rkhvstnv.pinplace.di.app.AppComponent

val Context.appComponent: AppComponent
    get() = when(this){
        is PinPlaceApplication -> appComponent
        else -> this.applicationContext.appComponent
    }