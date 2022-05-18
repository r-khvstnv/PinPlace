package com.rkhvstnv.pinplace.ui.addplace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class AddPlaceViewModel @Inject constructor() : ViewModel() {
    val imagePath = MutableLiveData<String>()
    private var dateL = MutableLiveData<Long>()
    private var lat = MutableLiveData<Double>()
    private var lon = MutableLiveData<Double>()

    fun setDateL(value: Long){
        dateL.value = value
    }

    fun setLatLon(lat: Double, lon: Double){
        this.lat.value = lat
        this.lon.value = lon
    }
}