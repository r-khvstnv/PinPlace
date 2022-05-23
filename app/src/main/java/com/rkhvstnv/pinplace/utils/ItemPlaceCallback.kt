package com.rkhvstnv.pinplace.utils

import com.rkhvstnv.pinplace.database.PlaceEntity

interface ItemPlaceCallback {
    fun onClick(id: Int)
    fun onDelete(placeEntity: PlaceEntity)
}