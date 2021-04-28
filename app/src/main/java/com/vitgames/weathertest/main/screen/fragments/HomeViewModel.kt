package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.TodayWeather
import com.vitgames.weathertest.main.support.CoroutineViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext


class HomeViewModel(private val context: Context) : CoroutineViewModel()  {
    var weatherData: String = ""
    var api: ApiWeather.ApiInterface? = null
    val progressLiveData = MutableLiveData<Boolean>()

    fun weatherResponse(Lat: Double, Lon: Double) {
        launch {
            api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
            val units = "metric"
            val key = "b94993855600d53aeb95fa8058c89494"
            val callToday: Call<TodayWeather?>? = api!!.getToday(Lat, Lon, units, key)
            callToday?.enqueue(object : Callback<TodayWeather?> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    call: Call<TodayWeather?>?,
                    response: Response<TodayWeather?>?
                ) {
                    val data: TodayWeather? = response!!.body()
                    if (response.isSuccessful) {
                        weatherData = data?.getCity() + " " + data?.getTempWithDegree()
                    }
                    progressLiveData.postValue(true)
                }

                override fun onFailure(call: Call<TodayWeather?>?, t: Throwable?) {
                    progressLiveData.postValue(false)
                    weatherData = "Failure to load current weather. Check you network"
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                    Log.e(ContentValues.TAG, "onFailure");
                    Log.e(ContentValues.TAG, t.toString());
                }
            })
        }
    }
}