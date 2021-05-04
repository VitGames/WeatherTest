package com.vitgames.weathertest.main.screen.fragments

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.support.Locator
import org.koin.android.viewmodel.ext.android.viewModel

class ForecastFragment : Fragment(R.layout.fragment_five_days) {

    private var location: Location? = null
    private var api: ApiWeather.ApiInterface? = null
    private var progressBarForecast: ProgressBar? = null
    private var textForecast: TextView? = null
    private val model: ForecastModel by viewModel()

    override fun onResume() {
        getForecastWeather()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
        return inflater.inflate(R.layout.fragment_five_days, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        progressBarForecast = view.findViewById(R.id.progressBarForecast)
        val btnUpdateForecast: ImageView = view.findViewById(R.id.btn_updateForecast)
        textForecast = view.findViewById(R.id.textForecast)
        getForecastWeather()
        model.forecastLiveData.observe(this.viewLifecycleOwner) { success ->
            progressBarForecast?.isVisible = success.not()
            if (success) {
                textForecast!!.text = model.forecastData
            }
        }
        btnUpdateForecast.setOnClickListener {
            getForecastWeather()
            btnUpdateForecast.animate().setDuration(600).rotationBy(540f).start()
            Toast.makeText(context, "Update..", Toast.LENGTH_SHORT)
                .show()
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getForecastWeather() {
        Handler().postDelayed(
            {
                location = (activity as Locator?)?.getLocationDouble()
                if (location != null) {
                    model.getForecastResponse(location)
                } else {
                    Log.e("LOCATION FORECAST", "Location null")
                }
            },
            500
        )
    }
}
