package com.rkhvstnv.pinplace.ui.weather

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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

    init {
        fetchWeatherData()
    }

    fun updateDishData(){
        fetchWeatherData()
    }

    private fun fetchWeatherData(){
        _inLoading.value = true

        compositeDisposable.add(
            weatherService.getWeather(41.0, 96.0)//todo test values, change fun declaration
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>(){
                    override fun onSuccess(t: WeatherModel) {
                        _inLoading.value = false
                        _weatherModel.value = t
                    }

                    override fun onError(e: Throwable) {
                        _inLoading.value = false
                        e.message?.let { Log.e("Test_message", it) }//todo
                    }

                })
        )

        RxJavaPlugins.setErrorHandler {
            e ->
            if (e is UndeliverableException){
                e.message?.let {
                    Log.e("Test_RxJava", it)//todo
                }
            }
        }
    }




}