package com.rkhvstnv.pinplace.utils

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rkhvstnv.pinplace.di.PinPlaceApplication
import com.rkhvstnv.pinplace.di.app.AppComponent

val Context.appComponent: AppComponent
    get() = when(this){
        is PinPlaceApplication -> appComponent
        else -> this.applicationContext.appComponent
    }

fun ImageView.loadImage(imageInt: Int){
    Glide.with(context).load(imageInt).centerInside().into(this)
}