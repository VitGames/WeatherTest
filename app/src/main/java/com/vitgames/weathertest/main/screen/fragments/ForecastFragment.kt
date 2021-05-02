package com.vitgames.weathertest.main.screen.fragments

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.support.Locator
import org.koin.android.viewmodel.ext.android.viewModel

// TODO forecastLinear observe live data || save in bundle
// TODO make design coolest
class ForecastFragment : Fragment(R.layout.fragment_five_days) {

    private var location: Location? = null
    private var api: ApiWeather.ApiInterface? = null
    private var forecastLinear: LinearLayout? = null
    private var progressBarForecast: ProgressBar? = null

    private val viewModel: ForecastViewModel by viewModel()

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
        forecastLinear = view.findViewById(R.id.llForecast)
        progressBarForecast = view.findViewById(R.id.progressBarForecast)
        viewModel.forecastLiveData.observe(this.viewLifecycleOwner) { success ->
            if (success.not()) {
                progressBarForecast?.isVisible = true
                forecastLinear = viewModel.forecastData
            } else {
                progressBarForecast?.isVisible = false
                forecastLinear = viewModel.forecastData
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getForecastWeather() {
        Handler().postDelayed(
            {
                location = (activity as Locator?)?.getLocationDouble()
                if (location != null) {
                    viewModel.getForecastResponse(location!!, forecastLinear)
                    forecastLinear = viewModel.forecastData
                } else {
                    Log.e("LOCATION FORECAST", "Location null")
                }
            },
            3000
        )
    }
}
