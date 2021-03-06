/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.rkhvstnv.pinplace.utils.Constants
import kotlinx.coroutines.flow.Flow


@Dao
interface PlaceDao {
    @Insert
    suspend fun insertPlace(place: PlaceEntity)

    @Delete
    suspend fun deletePlace(place: PlaceEntity)

    @Query("SELECT * FROM ${Constants.PLACE_TABLE_NAME} WHERE ${Constants.PLACE_ID} = :id")
    fun getPlaceById(id: Int): LiveData<PlaceEntity>

    @Query("SELECT * FROM ${Constants.PLACE_TABLE_NAME} ORDER BY ${Constants.PLACE_ID}")
    fun getAllPlaces(): Flow<List<PlaceEntity>>
}