package com.vitgames.weathertest.main.screen.fragments

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.api.ApiWeather
import com.vitgames.weathertest.main.support.Locator
import org.koin.android.viewmodel.ext.android.viewModel

class HomeFragment() : Fragment(R.layout.fragment_home) {
    private var location: Location? = null
    private var api: ApiWeather.ApiInterface? = null
    private var todayWeather: TextView? = null
    private var progressBar: ProgressBar? = null
    private val viewModel: HomeViewModel by viewModel()


    override fun onResume() {
        getTodayWeather()
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
        progressBar?.isVisible = false
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        todayWeather = view.findViewById(R.id.weatherToday)
        progressBar = view.findViewById(R.id.progressBarToday)
        progressBar?.isVisible = false
        viewModel.progressLiveData.observe(this.viewLifecycleOwner) { success ->
            if (success.not()) {
                progressBar?.isVisible = true
            } else {
                progressBar?.isVisible = false
                todayWeather?.text = viewModel.weatherData
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getTodayWeather() {
        Handler().postDelayed(
            {
                location = (activity as Locator?)?.getLocationDouble()
                if (location != null) {
                    viewModel.weatherResponse(location)
                } else {
                    Log.e("LOCATION TODAY", "Location null")
                }
            },
            3000
        )
    }
}