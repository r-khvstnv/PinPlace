package com.rkhvstnv.pinplace.ui.addplace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AddPlaceViewModel @Inject constructor(private val repository: PlaceRepository) : ViewModel() {
    val imagePath = MutableLiveData<String>()
    private var dateL = MutableLiveData<Long>()
    private var lat = MutableLiveData<Double>()
    private var lon = MutableLiveData<Double>()
    //checker for place saving
    private var _isSaved = MutableLiveData(false)
    val isSaved: MutableLiveData<Boolean> get() = _isSaved


    fun setDateL(value: Long){
        dateL.value = value
    }

    fun setLatLon(lat: Double, lon: Double){
        this.lat.value = lat
        this.lon.value = lon
    }

    /**Method insert new Place to database.
     * All fields, should be previously checked in Fragment*/
    fun addPlace(title: String, description: String, locationName: String){
        val place = PlaceEntity(
            imageSource = imagePath.value!!,
            title = title,
            description = description,
            locationName = locationName,
            lat = lat.value!!,
            lon = lon.value!!,
            date = dateL.value!!
        )

        viewModelScope.launch(Dispatchers.IO){
            repository.insertPlace(place)

            withContext(Dispatchers.Main){
                _isSaved.value = true
            }
        }
    }
}