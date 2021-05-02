package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.TodayWeather
import com.vitgames.weathertest.main.support.CoroutineViewModel
import com.vitgames.weathertest.main.support.Locator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val context: Context) : CoroutineViewModel() {
    private val tag: String = "WEATHER"
    var weatherData: String = ""
    private var api: ApiWeather.ApiInterface? = null
    val progressLiveData = MutableLiveData<Boolean>()

    fun weatherResponse(location: Location?) {
        launch {
            progressLiveData.postValue(false)
            val lat = location?.latitude
            val lon = location?.longitude
            api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
            val units = "metric"
            val key = "b94993855600d53aeb95fa8058c89494"
            val callToday: Call<TodayWeather?>? = api!!.getToday(lat, lon, units, key)
            callToday?.enqueue(object : Callback<TodayWeather?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<TodayWeather?>?,
                    response: Response<TodayWeather?>?
                ) {
                    progressLiveData.postValue(true)
                    val data: TodayWeather? = response!!.body()
                    if (response.isSuccessful) {
                        weatherData =
                            data?.getCity() + " " + data?.getTempWithDegree() + " \n " + data?.getDescription() + "\n" +
                                    "Wind: " + data?.getWind() + "\n" + "Direction: " + data?.getWindDeg() + "\n" +
                                    "Pressure: " + data?.getPressure() + " hPa"
                    }
                }

                override fun onFailure(call: Call<TodayWeather?>?, t: Throwable?) {
                    progressLiveData.postValue(true)
                    weatherData = "Failure to load current weather"
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                    Log.e(tag, "onFailure")
                    Log.e(tag, t.toString())
                }
            })
        }
    }
}