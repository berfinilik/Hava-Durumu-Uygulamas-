package com.berfinilik.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location

import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText

import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.berfinilik.weatherapp.adapter.WeatherToday
import com.berfinilik.weatherapp.databinding.ActivityMainBinding

import com.berfinilik.weatherapp.mvvm.HavaViewModel

import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    lateinit var havaViewModel: HavaViewModel

    lateinit var adapter: WeatherToday//Adaptör adı

    private lateinit var binding: ActivityMainBinding


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        havaViewModel = ViewModelProvider(this).get(HavaViewModel::class.java)



        havaViewModel.getWeather()


        adapter = WeatherToday()

        //sharedPrefs sııfından bir örnek oluşturup şehir değeri temizleme
        val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
        sharedPrefs.clearCityValue()

        havaViewModel.bugunkuHava.observe(this, Observer {

            val setNewlist = it as List<WeatherList>


            adapter.setList(setNewlist)
            binding.forecastRecyclerView.adapter = adapter

        })


        binding.lifecycleOwner = this
        binding.vm = havaViewModel



        //verideki sıcaklık değeri fahrenheit cinsinen alınıp celciusa çevrilir
        havaViewModel.yaklasikAyniHavaVerisi.observe(this, Observer {
            val temperatureFahrenheit = it!!.main?.temp
            val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
            val temperatureFormatted = String.format("%.2f", temperatureCelsius)

            for (i in it.weather) {
                binding.descMain.text = i.description
            }
            binding.tempMain.text = "$temperatureFormatted°"
            binding.humidityMain.text = it.main!!.humidity.toString()
            binding.windSpeed.text = it.wind?.speed.toString()

            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = inputFormat.parse(it.dtTxt!!)
            val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
            val dateanddayname = outputFormat.format(date!!)

            binding.dateDayMain.text = dateanddayname

            binding.chanceofrain.text = "${it.pop.toString()}%"



            for (i in it.weather) {

                if (i.icon == "01d") {
                    binding.imageMain.setImageResource(R.drawable.acikhavagunduz)
                }

                if (i.icon == "01n") {
                    binding.imageMain.setImageResource(R.drawable.onen)
                }

                if (i.icon == "02d") {
                    binding.imageMain.setImageResource(R.drawable.azbulutlugunduz)
                }

                if (i.icon == "02n") {
                    binding.imageMain.setImageResource(R.drawable.azbulutlugece)
                }
                if (i.icon == "03d" || i.icon == "03n") {
                    binding.imageMain.setImageResource(R.drawable.parcalibulutlu)
                }
                if (i.icon == "10d") {
                    binding.imageMain.setImageResource(R.drawable.gunduzyagmurlu)
                }
                if (i.icon == "10n") {
                    binding.imageMain.setImageResource(R.drawable.geceyagmurlu)
                }
                if (i.icon == "04d" || i.icon == "04n") {
                    binding.imageMain.setImageResource(R.drawable.kapalibulutlu)
                }
                if (i.icon == "09d" || i.icon == "09n") {
                    binding.imageMain.setImageResource(R.drawable.gunduzyagmurlufirtinali)
                }
                if (i.icon == "11d" || i.icon == "11n") {
                    binding.imageMain.setImageResource(R.drawable.gunduzgokgurultulufirtinali)
                }
                if (i.icon == "13d" || i.icon == "13n") {
                    binding.imageMain.setImageResource(R.drawable.karyagisi)
                }
                if (i.icon == "50d" || i.icon == "50n") {
                    binding.imageMain.setImageResource(R.drawable.sisli)
                }
            }
        })

        // konum kontrolü
        if (checkLocationPermissions()) {

            getCurrentLocation()
        } else {

            requestLocationPermissions()
        }

        val searchEditText = binding.searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(Color.WHITE)

        binding.next5Days.setOnClickListener {
            startActivity(Intent(this, ForeCastActivity::class.java))
        }


        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(query: String?): Boolean {

                val sharedPrefs = SharedPrefs.getInstance(this@MainActivity)
                sharedPrefs.setValueOrNull("city", query!!)

                if (!query.isNullOrEmpty()) {

                    havaViewModel.getWeather(query)

                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                    binding.searchView.isIconified = true
                }

                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }
    private fun checkLocationPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                coarseLocationPermission == PackageManager.PERMISSION_GRANTED
    }
    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            Utils.PERMISSION_REQUEST_CODE
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Utils.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED
            ) {

                getCurrentLocation()
            } else {

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val location: Location? =
                locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude

                val myprefs = SharedPrefs(this)
                myprefs.setValue("lon", longitude.toString())
                myprefs.setValue("lat", latitude.toString())


                Toast.makeText(this, "Latitude: $latitude, Longitude: $longitude", Toast.LENGTH_SHORT).show()


                Log.d("Current Location", "Latitude: $latitude, Longitude: $longitude")



            } else {

            }
        } else {

        }
    }



}

