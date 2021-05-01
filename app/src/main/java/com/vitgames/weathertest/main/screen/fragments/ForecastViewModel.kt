package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.TodayWeather
import com.vitgames.weathertest.main.models.WeatherForecast
import com.vitgames.weathertest.main.support.CoroutineViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

//TODO add weather description
@SuppressLint("StaticFieldLeak")
class ForecastViewModel(private val context: Context) : CoroutineViewModel() {

    private val TAG = "WEATHER"
    private var api: ApiWeather.ApiInterface? = null
    var forecastData: LinearLayout? = LinearLayout(context)
    val forecastLiveData = MutableLiveData<Boolean>()

    fun getForecastResponse(location: Location, linearLayout: LinearLayout?) {
        launch {
            forecastLiveData.postValue(false)
            val lat = location.latitude
            val lon = location.longitude
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
                    forecastLiveData.postValue(true)
                    val data: WeatherForecast? = response!!.body()
                    if (response.isSuccessful) {
                        val formatDayOfWeek = SimpleDateFormat("E")
                        linearLayout?.removeAllViews()
                        for (TodayWeather in data?.getItems()!!) {
                            val childLayout = LinearLayout(context)
                            childLayout.orientation = LinearLayout.VERTICAL

                            val tvDay = TextView(context)
                            val dayOfWeek = formatDayOfWeek.format(TodayWeather.getDate().time)
                            tvDay.text = dayOfWeek + " : " + TodayWeather.getDtTxt().toString()
                            tvDay.textSize = 18f
                            tvDay.width = ViewGroup.LayoutParams.WRAP_CONTENT
                            tvDay.setTextColor(
                                ContextCompat.getColor(context, R.color.black)
                            )
                            childLayout.addView(tvDay)

                            val tvTemp = TextView(context)
                            tvTemp.text = TodayWeather.getTempWithDegree()
                            tvTemp.textSize = 20f
                            tvTemp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                            tvTemp.textAlignment = View.TEXT_ALIGNMENT_CENTER
                            tvTemp.setTextColor(
                                ContextCompat.getColor(context, R.color.black)
                            )

                            childLayout.addView(tvTemp)
                            linearLayout?.addView(childLayout)
                        }
                        forecastData = linearLayout
                    }
                }

                override fun onFailure(call: Call<WeatherForecast?>?, t: Throwable?) {
                    forecastLiveData.postValue(true)
                    Log.e(TAG, "onFailure");
                    Log.e(TAG, t.toString());
                }
            })
        }
    }
}