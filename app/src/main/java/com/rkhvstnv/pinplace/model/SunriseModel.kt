/************************************************
 * Created by Ruslan Khvastunov                 *
 * r.khvastunov@gmail.com                       *
 * Copyright (c) 2022                           *
 * All rights reserved.                         *
 *                                              *
 ************************************************/

package com.rkhvstnv.pinplace.model

data class SunriseModel(
    val sunrise: String,
    val sunset: String,
    val curProgress: Int,
    val maxProgress: Int
    )