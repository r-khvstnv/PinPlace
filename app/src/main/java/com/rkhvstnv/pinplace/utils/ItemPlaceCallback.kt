/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.utils

import com.rkhvstnv.pinplace.database.PlaceEntity

interface ItemPlaceCallback {
    fun onClick(id: Int)
    fun onDelete(placeEntity: PlaceEntity)
}