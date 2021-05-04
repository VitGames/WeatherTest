package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.location.Location
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.WeatherForecast
import com.vitgames.weathertest.main.support.CoroutineModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

@SuppressLint("StaticFieldLeak")
class ForecastModel(private val context: Context) : CoroutineModel() {

    private val tag = "WEATHER"
    private var api: ApiWeather.ApiInterface? = null
    var forecastData: String = ""
    val forecastLiveData = MutableLiveData<Boolean>()

    fun getForecastResponse(location: Location?) {
        launch {
            forecastLiveData.postValue(false)
            val lat = location?.latitude
            val lon = location?.longitude
            api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
            val units = "metric"
            val key = "b94993855600d53aeb95fa8058c89494"
            val callForecast: Call<WeatherForecast?>? = api!!.getForecast(lat, lon, units, key)
            callForecast?.enqueue(object : Callback<WeatherForecast?> {
                @SuppressLint("SimpleDateFormat", "DefaultLocale", "ResourceAsColor", "SetTextI18n")
                override fun onResponse(
                    call: Call<WeatherForecast?>?,
                    response: Response<WeatherForecast?>?
                ) {
                    val data: WeatherForecast? = response!!.body()
                    if (response.isSuccessful) {
                        val formatDayOfWeek = SimpleDateFormat("E")
                        for (TodayWeather in data?.getItems()!!) {
                            /** dayOfWeek depense of system language*/
                            val dayOfWeek = formatDayOfWeek.format(TodayWeather.getDate().time)
                            val str1: String =
                                dayOfWeek + " : " + TodayWeather.getDtTxt().toString()
                            forecastData += str1 + "\n"
                            val str2: String =
                                TodayWeather.getDescription() + " : " + TodayWeather.getTempWithDegree()
                            forecastData += str2 + "\n"
                        }
                        forecastLiveData.postValue(true)
                    }
                }

                override fun onFailure(call: Call<WeatherForecast?>?, t: Throwable?) {
                    forecastLiveData.postValue(true)
                    Log.e(tag, "onFailure");
                    Log.e(tag, t.toString());
                }
            })
        }
    }
}