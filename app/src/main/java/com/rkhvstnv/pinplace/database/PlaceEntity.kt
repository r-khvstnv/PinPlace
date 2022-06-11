/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rkhvstnv.pinplace.utils.Constants

@Entity(tableName = Constants.PLACE_TABLE_NAME)
data class PlaceEntity(
    @ColumnInfo val imageSource: String,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo val locationName: String,
    @ColumnInfo val lat: Double,
    @ColumnInfo val lon: Double,
    @ColumnInfo val date: Long,

    @PrimaryKey(autoGenerate = true) var id: Int = 0
)