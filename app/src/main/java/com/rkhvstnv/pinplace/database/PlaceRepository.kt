/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.database

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class PlaceRepository @Inject constructor(private val dao: PlaceDao){
    suspend fun insertPlace(placeEntity: PlaceEntity){
        dao.insertPlace(placeEntity)
    }

    suspend fun deletePlace(placeEntity: PlaceEntity){
        dao.deletePlace(placeEntity)
    }

    fun getPlaceById(id: Int) = dao.getPlaceById(id = id)

    val allPlacesList: Flow<List<PlaceEntity>> = dao.getAllPlaces()
}