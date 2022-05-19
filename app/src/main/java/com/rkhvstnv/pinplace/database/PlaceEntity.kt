package com.rkhvstnv.pinplace.database

data class PlaceEntity(
    val imageSource: String,
    val title: String,
    val description: String,
    val locationName: String,
    val lat: Double,
    val long: Double,
    val date: Long,
    var id: Int = 0
)