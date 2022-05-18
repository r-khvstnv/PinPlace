package com.rkhvstnv.pinplace.model

data class PlaceModel(
    val imageSource: String,
    val title: String,
    val description: String,
    val locationName: String,
    val lat: Double,
    val long: Double,
    val date: Long,
    var id: Int = 0
)