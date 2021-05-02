package com.vitgames.weathertest.main.screen.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.models.WeatherForecast
import com.vitgames.weathertest.main.support.CoroutineViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

@SuppressLint("StaticFieldLeak")
class ForecastViewModel(private val context: Context) : CoroutineViewModel() {

    private val tag = "WEATHER"
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
                @RequiresApi(Build.VERSION_CODES.O)
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
                            tvDay.typeface = Typeface.DEFAULT_BOLD
                            tvDay.width = ViewGroup.LayoutParams.WRAP_CONTENT
                            tvDay.setTextColor(
                                ContextCompat.getColor(context, R.color.black)
                            )
                            childLayout.addView(tvDay)

                            val tvTemp = TextView(context)
                            tvTemp.text =
                                TodayWeather.getDescription() + " : " + TodayWeather.getTempWithDegree()
                            tvTemp.textSize = 20f
                            tvTemp.setTypeface(null, Typeface.BOLD_ITALIC)
                            tvTemp.width = ViewGroup.LayoutParams.WRAP_CONTENT
                            tvTemp.textAlignment = View.TEXT_ALIGNMENT_GRAVITY
                            tvTemp.setTextColor(
                                ContextCompat.getColor(context, R.color.blue)
                            )

                            childLayout.addView(tvTemp)
                            linearLayout?.addView(childLayout)
                        }
                        forecastData = linearLayout
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