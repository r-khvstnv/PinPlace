package com.rkhvstnv.pinplace.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.rkhvstnv.pinplace.di.PinPlaceApplication
import com.rkhvstnv.pinplace.di.component.AppComponent

val Context.appComponent: AppComponent
    get() = when(this){
        is PinPlaceApplication -> appComponent
        else -> this.applicationContext.appComponent
    }

fun ImageView.loadIntImage(int: Int){
    Glide.with(context).load(int).centerInside().into(this)
}

fun ImageView.loadBitmapImage(image: Bitmap){
    Glide.with(context).load(image).centerCrop().into(this)
}
