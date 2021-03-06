package com.example.weatherapplication.service

import com.example.weatherapplication.model.WeatherModel
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
    //http://api.openweathermap.org/data/2.5/weather?q=bingöl&APPID=fdf871cedaf3413c6a23230372c30a02

    @GET("data/2.5/weather?&units=metric&APPID=04a42b96398abc8e4183798ed22f9485")
    fun getData(
            @Query("lat") latitude: Double,
            @Query("lon") longitude: Double
    ): Single<WeatherModel>


    @GET("data/2.5/weather?&units=metric&APPID=04a42b96398abc8e4183798ed22f9485")
    fun getDataCity(
        @Query("q") latitude: String,
    ): Single<WeatherModel>
}