package com.berfinilik.weatherapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.berfinilik.weatherapp.adapter.ForeCastAdapter

import com.berfinilik.weatherapp.R
import com.berfinilik.weatherapp.mvvm.HavaViewModel

class ForeCastActivity : AppCompatActivity() {

    private lateinit var adapterForeCastAdapter: ForeCastAdapter
    lateinit var viM : HavaViewModel
    lateinit var rvForeCast: RecyclerView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fore_cast)

        viM = ViewModelProvider(this).get(HavaViewModel::class.java)
        adapterForeCastAdapter = ForeCastAdapter()
        rvForeCast = findViewById<RecyclerView>(R.id.rvHavaTahmini)

        val sharedPrefs = SharedPrefs.getInstance(this)
        val city = sharedPrefs.getValueOrNull("city")

        Log.d("Prefs", city.toString())

        //şehir belirlenmişse vim aracılığıyla hava tahminleri alınır

        if (city!=null){
            viM.getGelecekHavaTahmini(city)
        } else {
            viM.getGelecekHavaTahmini()
        }

        //veriler güncellendiğinde yeni liste adaptöre atanır ve recyclerview güncellenir

        viM.tahminiHavaLiveData.observe(this, Observer {

            val setNewlist = it as List<WeatherList>

            Log.d("Forecast LiveData", setNewlist.toString())

            adapterForeCastAdapter.setList(setNewlist)
            rvForeCast.adapter = adapterForeCastAdapter
        })
    }
}