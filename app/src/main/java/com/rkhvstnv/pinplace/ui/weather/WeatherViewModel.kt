package com.rkhvstnv.pinplace.ui.weather

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.rkhvstnv.pinplace.api.WeatherService
import com.rkhvstnv.pinplace.model.WeatherModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.exceptions.UndeliverableException
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class WeatherViewModel @Inject constructor(
    private val weatherService: WeatherService
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var _weatherModel = MutableLiveData<WeatherModel>()
    private var _inLoading = MutableLiveData<Boolean>()
    private var _loadingError = MutableLiveData<String>()
    val weatherModel get() = _weatherModel
    val inLoading get() = _inLoading
    val loadingError get() = _loadingError

    /**Method that can be called from view
     * to request weather data based on provided latitude and longitude*/
    fun updateCurrentLocation(lat: Double, lon: Double){
        fetchWeatherData(lat, lon)
    }

    /**Method handles Api call to receive weather data */
    private fun fetchWeatherData(_lat: Double, _lon: Double){
        //set loading state
        _inLoading.value = true

        compositeDisposable.add(
            weatherService.getWeather(_lat, _lon)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>(){
                    /*on Success change loading state and fetch weather data*/
                    override fun onSuccess(t: WeatherModel) {
                        _inLoading.value = false
                        _weatherModel.value = t
                    }

                    /*on Error change loading state and get error message*/
                    override fun onError(e: Throwable) {
                        _inLoading.value = false
                        e.message?.let { _loadingError.value = it }
                    }

                })
        )

        /*Method handles error specific to RxJava (UndeliverableExceptions)*/
        RxJavaPlugins.setErrorHandler {
            e ->
            if (e is UndeliverableException){
                e.message?.let {
                    Log.e("Test_RxJava", it)
                }
            }
        }
    }


    /** Json data should be updated after every Api call. After it will be saved in shared preferences
     * (This is implemented in the fragment itself).
     * It helps user to gain better UX. Because when he opens weather fragment,
     * user will already see loaded data (that will be later up to date, after successful Api call),
     * instead of empty container*/

    /**Method fetch weather data from Json String.*/
    fun setWeatherModelFromJson(model: String?){
        if (model != null){
            _weatherModel.value = Gson().fromJson(model, WeatherModel::class.java)
        }
    }
}