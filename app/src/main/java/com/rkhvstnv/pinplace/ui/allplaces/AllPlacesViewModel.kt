package com.rkhvstnv.pinplace.ui.allplaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AllPlacesViewModel @Inject constructor(private val repository: PlaceRepository) : ViewModel() {
    val allPlaces: LiveData<List<PlaceEntity>> = repository.allPlacesList.asLiveData()

    /**Method deletes place from database*/
    fun requestPlaceDeleting(placeEntity: PlaceEntity){
        viewModelScope.launch(Dispatchers.IO){
            repository.deletePlace(placeEntity)
        }
    }
}