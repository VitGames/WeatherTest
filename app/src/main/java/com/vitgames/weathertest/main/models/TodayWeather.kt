package com.vitgames.weathertest.main.models

import com.google.gson.annotations.SerializedName
import java.util.*


class TodayWeather {

    class WeatherTemp {
        var temp: Double? = null
        var temp_min: Double? = null
        var temp_max: Double? = null
        var pressure: Double? = null
    }

    class Wind {
        var speed: Double? = null
        var deg: Double? = null
    }

    class WeatherDescription {
        var description: String? = null
    }

    @SerializedName("main")
    private var main: WeatherTemp? = null

    @SerializedName("weather")
    private var weather: List<WeatherDescription>? = null

    @SerializedName("name")
    private val city: String? = null

    @SerializedName("wind")
    private var wind: Wind? = null

    @SerializedName("dt")
    private val timestamp: Long = 0

    @SerializedName("dt_txt")
    private var dt_txt: String? = null

//    //WeatherDay
//    fun TodayWeather(temp: WeatherTemp?, desctiption: List<WeatherDescription?>?) {
//        this.temp = temp
//        this.desctiption = desctiption as List<WeatherDescription>?
//    }

    fun getDate(): Calendar {
        val date: Calendar = Calendar.getInstance()
        date.timeInMillis = timestamp * 1000
        return date
    }

    fun getDescription(): String {
        val string = weather!![0].description
        return string?.capitalize(Locale.ENGLISH).toString()
    }

    fun getWind(): String {
        return wind!!.speed!!.toInt().toString() + " m/s"
    }

    fun getWindDeg(): String {
        return wind!!.deg.toString() + "\u00B0"
    }

    fun getPressure(): Double? {
        return main!!.pressure
    }

    fun getTemp(): String {
        return main!!.temp.toString()
    }

    fun getTempMin(): String {
        return main!!.temp_min.toString()
    }

    fun getTempMax(): String {
        return main!!.temp_max.toString()
    }

    fun getTempInteger(): String {
        return main!!.temp!!.toInt().toString()
    }

    fun getTempWithDegree(): String {
        return main!!.temp!!.toInt().toString() + "\u00B0"
    }

    fun getCity(): String? {
        return city
    }

    fun getDtTxt(): String? {
        return dt_txt
    }
}