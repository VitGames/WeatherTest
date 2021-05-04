package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.TodayWeather
import com.vitgames.weathertest.main.support.CoroutineModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeModel(private val context: Context) : CoroutineModel() {
    private val tag: String = "WEATHER"
    private var weatherData: String = ""
    private var api: ApiWeather.ApiInterface? = null
    val progressLiveData = MutableLiveData<Boolean>()
    var weatherLiveData = MutableLiveData<String>()

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
                        weatherLiveData.postValue(weatherData)
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