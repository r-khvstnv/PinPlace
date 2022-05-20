package com.rkhvstnv.pinplace.ui.placedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlaceDetailsViewModel @Inject constructor(private val repository: PlaceRepository): ViewModel() {
    lateinit var place: LiveData<PlaceEntity>
    private val _isDeleted = MutableLiveData<Boolean>(false)
    val isDeleted: LiveData<Boolean> get() = _isDeleted

    fun requestPlace(id: Int){
        place = repository.getPlaceById(id = id)
    }

    fun requestPlaceDeleting(){
        if (place.value != null){
            viewModelScope.launch(Dispatchers.IO){
                repository.deletePlace(place.value!!)
                _isDeleted.value = true
            }
        }
    }
}