package com.berfinilik.weatherapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.berfinilik.weatherapp.R
import com.berfinilik.weatherapp.WeatherList

import java.text.SimpleDateFormat

import java.util.*

class ForeCastAdapter : RecyclerView.Adapter<ForeCastHolder>() {


    private var listofforecast = listOf<WeatherList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForeCastHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.upcomingforecastlist, parent, false)
        return ForeCastHolder(view)
    }

    override fun getItemCount(): Int {
        return listofforecast.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ForeCastHolder, position: Int) {
        val forecastObject = listofforecast[position]

        for (i in forecastObject.weather){

            holder.description.text = i.description!!
        }

        holder.nem.text = forecastObject.main!!.humidity.toString()
        holder.ruzgarHizi.text = forecastObject.wind?.speed.toString()

        val temperatureFahrenheit = forecastObject.main?.temp
        val temperatureCelsius = (temperatureFahrenheit?.minus(273.15))
        val temperatureFormatted = String.format("%.2f", temperatureCelsius)
        holder.sicaklik.text = "$temperatureFormatted °C"
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(forecastObject.dtTxt!!)
        val outputFormat = SimpleDateFormat("d MMMM EEEE", Locale.getDefault())
        val dateanddayname = outputFormat.format(date!!)
        holder.dateDayName.text = dateanddayname
        for (i in forecastObject.weather) {
            if (i.icon == "01d") {
                holder.resimGrafik.setImageResource(R.drawable.acikhavagunduz)
                holder.smallIcon.setImageResource(R.drawable.acikhavagunduz)
            }
            if (i.icon == "01n") {
                holder.resimGrafik.setImageResource(R.drawable.onen)
                holder.smallIcon.setImageResource(R.drawable.onen)
            }
            if (i.icon == "02d") {
                holder.resimGrafik.setImageResource(R.drawable.azbulutlugunduz)
                holder.smallIcon.setImageResource(R.drawable.azbulutlugunduz)
            }
            if (i.icon == "02n") {
                holder.resimGrafik.setImageResource(R.drawable.azbulutlugece)
                holder.smallIcon.setImageResource(R.drawable.parcalibulutlu)
            }
            if (i.icon == "10d") {
                holder.resimGrafik.setImageResource(R.drawable.gunduzyagmurlu)
                holder.smallIcon.setImageResource(R.drawable.gunduzyagmurlu)
            }
            if (i.icon == "10n") {
                holder.resimGrafik.setImageResource(R.drawable.geceyagmurlu)
                holder.smallIcon.setImageResource(R.drawable.geceyagmurlu)
            }
            if (i.icon == "04d" || i.icon == "04n") {
                holder.resimGrafik.setImageResource(R.drawable.kapalibulutlu)
                holder.smallIcon.setImageResource(R.drawable.kapalibulutlu)
            }
            if (i.icon == "09d" || i.icon == "09n") {
                holder.resimGrafik.setImageResource(R.drawable.gunduzyagmurlufirtinali)
                holder.smallIcon.setImageResource(R.drawable.gunduzyagmurlufirtinali)
            }
            if (i.icon == "11d" || i.icon == "11n") {
                holder.resimGrafik.setImageResource(R.drawable.gunduzgokgurultulufirtinali)
                holder.smallIcon.setImageResource(R.drawable.gunduzgokgurultulufirtinali)
            }
            if (i.icon == "13d" || i.icon == "13n") {
                holder.resimGrafik.setImageResource(R.drawable.karyagisi)
                holder.smallIcon.setImageResource(R.drawable.karyagisi)
            }
            if (i.icon == "50d" || i.icon == "50n") {
                holder.resimGrafik.setImageResource(R.drawable.sisli)
                holder.smallIcon.setImageResource(R.drawable.sisli)
            }
        }
    }
    fun setList(newlist: List<WeatherList>) {
        this.listofforecast = newlist
    }
}
class ForeCastHolder(itemView: View) : ViewHolder(itemView){
    val resimGrafik: ImageView = itemView.findViewById(R.id.imageGraphic)
    val description : TextView = itemView.findViewById(R.id.weatherDescr)
    val nem : TextView = itemView.findViewById(R.id.humidity)
    val ruzgarHizi : TextView = itemView.findViewById(R.id.windSpeed)
    val sicaklik : TextView = itemView.findViewById(R.id.tempDisplayForeCast)
    val smallIcon : ImageView = itemView.findViewById(R.id.smallIcon)
    val dateDayName : TextView = itemView.findViewById(R.id.dayDateText)
}