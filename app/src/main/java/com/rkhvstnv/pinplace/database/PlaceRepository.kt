package com.rkhvstnv.pinplace.database

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
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