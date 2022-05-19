package com.rkhvstnv.pinplace.ui.allplaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import javax.inject.Inject

class AllPlacesViewModel @Inject constructor(repository: PlaceRepository) : ViewModel() {
    val allPlaces: LiveData<List<PlaceEntity>> = repository.allPlacesList.asLiveData()
}