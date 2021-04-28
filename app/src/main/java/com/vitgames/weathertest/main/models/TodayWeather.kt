package com.vitgames.weathertest.main.models

import com.google.gson.annotations.SerializedName


class TodayWeather {

    class WeatherTemp {
        var temp: Double? = null
        var temp_min: Double? = null
        var temp_max: Double? = null
    }

    class WeatherDescription {
        var icon: String? = null
    }

    @SerializedName("main")
    private var temp: WeatherTemp? = null

    @SerializedName("weather")
    private var desctiption: List<WeatherDescription>? = null

    @SerializedName("name")
    private val city: String? = null

    @SerializedName("dt")
    private val timestamp: Long = 0

    //WeatherDay
    fun TodayWeather(temp: WeatherTemp?, desctiption: List<WeatherDescription?>?) {
        this.temp = temp
        this.desctiption = desctiption as List<WeatherDescription>?
    }

    fun getTemp(): String {
        return temp!!.temp.toString()
    }

    fun getTempMin(): String {
        return temp!!.temp_min.toString()
    }

    fun getTempMax(): String {
        return temp!!.temp_max.toString()
    }

    fun getTempInteger(): String {
        return temp!!.temp!!.toInt().toString()
    }

    fun getTempWithDegree(): String {
        return temp!!.temp!!.toInt().toString() + "\u00B0"
    }

    fun getCity(): String? {
        return city
    }



}