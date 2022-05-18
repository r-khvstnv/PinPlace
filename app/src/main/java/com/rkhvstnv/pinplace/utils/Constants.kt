package com.rkhvstnv.pinplace.utils

object Constants {
    //weather api
    const val WEATHER_API_ENDPOINT = "data/2.5/onecall?"
    const val WEATHER_API_BASE_URL = "https://api.openweathermap.org/"
    const val WEATHER_API_LAT = "lat"
    const val WEATHER_API_LON = "lon"
    const val WEATHER_API_UNITS = "units"
    const val WEATHER_API_UNITS_VALUE = "metric"
    const val WEATHER_API_EXCLUDE = "exclude"
    const val WEATHER_API_EXCLUDED_VALUE = "minutely,alerts"
    const val WEATHER_API_ID = "appid"
    //weather shared preferences
    const val WEATHER_SHARED_PREF = "WeatherSharedPref"
    const val WEATHER_SHARED_JSON_STRING = "WeatherSharedJsonString"



    const val PLACE_IMAGE_DIRECTORY = "PinPlaceImage"
}