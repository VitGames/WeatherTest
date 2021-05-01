package com.vitgames.weathertest.main.api

import com.vitgames.weathertest.main.models.TodayWeather
import com.vitgames.weathertest.main.models.WeatherForecast
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//api.openweathermap.org/data/2.5/forecast?lat=${LAT}&lon=${LON}&appid=${API}
class ApiWeather {
    //WeatherAPI
    private val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private var retrofit: Retrofit? = null

    interface ApiInterface {
        @GET("weather")
        fun getToday(
            @Query("lat") lat: Double?,
            @Query("lon") lon: Double?,
            @Query("units") units: String?,
            @Query("appid") appid: String?
        ): Call<TodayWeather?>?

        @GET("forecast")
        fun getForecast(
            @Query("lat") lat: Double?,
            @Query("lon") lon: Double?,
            @Query("units") units: String?,
            @Query("appid") appid: String?
        ): Call<WeatherForecast?>?
    }

    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }
}