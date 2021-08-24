package com.example.weatherapplication.service

import com.example.weatherapplication.model.WeatherModel
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class WeatherAPIService {
    //http://api.openweathermap.org/data/2.5/weather?q=bingol&APPID=fdf871cedaf3413c6a23230372c30a02

    private val BASE_URL = "http://api.openweathermap.org/"

    private val api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(WeatherAPI::class.java)

    fun getDataService(lat: Double, long: Double): Single<WeatherModel> {
        return api.getData(lat,long)
    }

    fun getDataServiceCity(city: String): Single<WeatherModel> {
        return api.getDataCity(city)
    }
}