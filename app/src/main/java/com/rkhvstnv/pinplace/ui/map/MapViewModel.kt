/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.rkhvstnv.pinplace.database.PlaceEntity
import com.rkhvstnv.pinplace.database.PlaceRepository
import javax.inject.Inject

class MapViewModel @Inject constructor(private val repository: PlaceRepository) : ViewModel() {
    val allPlacesList: LiveData<List<PlaceEntity>> = repository.allPlacesList.asLiveData()
}