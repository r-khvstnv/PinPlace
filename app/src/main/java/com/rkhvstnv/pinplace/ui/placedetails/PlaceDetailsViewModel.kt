package com.rkhvstnv.pinplace.ui.placedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaceDetailsViewModel @Inject constructor(private val repository: PlaceRepository): ViewModel() {
    lateinit var place: LiveData<PlaceEntity>
    private val _isDeleted = MutableLiveData(false)
    val isDeleted: LiveData<Boolean> get() = _isDeleted
    private val _isMapVisible = MutableLiveData(false)
    val isMapVisible: LiveData<Boolean> get() = _isMapVisible

    fun requestPlace(id: Int){
        place = repository.getPlaceById(id = id)
    }

    fun requestPlaceDeleting(){
        place.value?.let {
            viewModelScope.launch(Dispatchers.IO){
                repository.deletePlace(it)
                _isDeleted.postValue(true)
            }
        }
    }

    fun flipMapVisibilityState(){
        _isMapVisible.value?.let {
            _isMapVisible.value = !it
        }
    }
}