package com.example.weatherapplication.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weatherapplication.model.WeatherModel
import com.example.weatherapplication.service.WeatherAPIService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    private val weatherApiService = WeatherAPIService()
    private val disposable = CompositeDisposable()

    val weather_data = MutableLiveData<WeatherModel>()
    val weather_error = MutableLiveData<Boolean>()
    val weather_loading = MutableLiveData<Boolean>()

    fun refreshData(lat: Double, long: Double) {
        getDataFromAPI(lat, long)
    }

    fun refreshDataSearch(city: String) {
        getDataFromAPICity(city)
    }

    private fun getDataFromAPI(lat: Double, long: Double) {
        weather_loading.value = true
        disposable.add(
            weatherApiService.getDataService(lat,long)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {

                    override fun onSuccess(t: WeatherModel) {
                        weather_data.value = t
                        weather_error.value = false
                        weather_loading.value = false
                        Log.d(TAG, "onSuccess: Success")
                    }

                    override fun onError(e: Throwable) {
                        weather_error.value = true
                        weather_loading.value = false
                        Log.e(TAG, "onError: " + e)
                    }

                })
        )
    }

    private fun getDataFromAPICity(city: String) {
        weather_loading.value = true
        disposable.add(
            weatherApiService.getDataServiceCity(city)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<WeatherModel>() {

                    override fun onSuccess(t: WeatherModel) {
                        weather_data.value = t
                        weather_error.value = false
                        weather_loading.value = false
                        Log.d(TAG, "onSuccess: Success")
                    }

                    override fun onError(e: Throwable) {
                        weather_error.value = true
                        weather_loading.value = false
                        Log.e(TAG, "onError: " + e)
                    }

                })
        )
    }

}