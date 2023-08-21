package com.berfinilik.weatherapp.mvvm

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.berfinilik.weatherapp.MyApplication
import com.berfinilik.weatherapp.SharedPrefs
import com.berfinilik.weatherapp.WeatherList
import com.berfinilik.weatherapp.service.RetrofitInstance



import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
class HavaViewModel : ViewModel() {

    val bugunkuHava = MutableLiveData<List<WeatherList>>()
    val tahminiHavaLiveData = MutableLiveData<List<WeatherList>>()

    val yaklasikAyniHavaVerisi = MutableLiveData<WeatherList?>()
    val cityName = MutableLiveData<String>()
    val context = MyApplication.instance

    fun getWeather(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val todayWeatherList = mutableListOf<WeatherList>()
        val currentDateTime = LocalDateTime.now()
        val currentDateO = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val sharedPrefs = SharedPrefs.getInstance(context)
        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()
        Log.e("ViewModel", "$lat $lon")
        val call = if (city != null) {
            RetrofitInstance.api.getWeatherByCity(city)
        } else {
            RetrofitInstance.api.getCurrentWeather(lat, lon)
        }
        val response = call.execute()
        if (response.isSuccessful) {
            val weatherList = response.body()?.weatherList
            cityName.postValue(response.body()?.city!!.name)
            val currentDate = currentDateO
            weatherList?.forEach { weather ->
                if (weather.dtTxt!!.split("\\s".toRegex()).contains(currentDate)) {
                    todayWeatherList.add(weather)
                }
            }
            val enYakinHava = enYakinHavayiBul(todayWeatherList)
            yaklasikAyniHavaVerisi.postValue(enYakinHava)
            bugunkuHava.postValue(todayWeatherList)
        } else {
            val errorMessage = response.message()
            Log.e("Mevcut Hava Durumu Hatası", "Error: $errorMessage")
        }
    }

    fun getGelecekHavaTahmini(city: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val forecastWeatherList = mutableListOf<WeatherList>()
        val currentDateTime = LocalDateTime.now()
        val currentDateO = currentDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val sharedPrefs = SharedPrefs.getInstance(context)
        val lat = sharedPrefs.getValue("lat").toString()
        val lon = sharedPrefs.getValue("lon").toString()

        val call = if (city != null) {
            RetrofitInstance.api.getWeatherByCity(city)
        } else {
            RetrofitInstance.api.getCurrentWeather(lat, lon)
        }

        val response = call.execute()

        if (response.isSuccessful) {
            val weatherList = response.body()?.weatherList
            val currentDate = currentDateO
            weatherList?.forEach { weather ->

                if (!weather.dtTxt!!.split("\\s".toRegex()).contains(currentDate)) {
                    if (weather.dtTxt!!.substring(11, 16) == "12:00") {
                        forecastWeatherList.add(weather)
                    }
                }
            }

            tahminiHavaLiveData.postValue(forecastWeatherList)
            Log.d("Tahmin LiveData",tahminiHavaLiveData.value.toString())
        } else {
            val errorMessage = response.message()
            Log.e("SimdikiHavaError", "Error: $errorMessage")
        }
    }
    private fun enYakinHavayiBul(weatherList: List<WeatherList>): WeatherList? {
        val systemTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        var enYakinHava: WeatherList? = null
        var minzamanFarki = Int.MAX_VALUE

        for (weather in weatherList) {
            val weatherTime = weather.dtTxt!!.substring(11, 16)
            val zamanFarki = Math.abs(zamanDakikayaCevir(weatherTime) - zamanDakikayaCevir(systemTime))

            if (zamanFarki < minzamanFarki) {

                minzamanFarki = zamanFarki
                enYakinHava = weather
            }
        }
        return enYakinHava
    }

    private fun zamanDakikayaCevir(time: String): Int {
        val parts = time.split(":")
        return parts[0].toInt() * 60 + parts[1].toInt()
    }
}