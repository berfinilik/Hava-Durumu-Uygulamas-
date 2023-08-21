package com.berfinilik.weatherapp.service


import com.berfinilik.weatherapp.ForeCast
import com.berfinilik.weatherapp.Utils

import retrofit2.Call

import retrofit2.http.GET
import retrofit2.http.Query

interface Service {
    //koordinatlara göre güncel hava durumu bilgisi
    @GET("forecast?")
    fun getCurrentWeather(
        @Query("lat")
        lat: String,
        @Query("lon")
        lon: String,
        @Query("appid")
        appid: String = Utils.API_KEY

    ): Call<ForeCast>


    //belirtilen şehire göre hava durumu bilgisi
    @GET("forecast?")
    fun getWeatherByCity(
        @Query("q")
        city: String,
        @Query("appid")
        appid: String = Utils.API_KEY

    ): Call<ForeCast>

}