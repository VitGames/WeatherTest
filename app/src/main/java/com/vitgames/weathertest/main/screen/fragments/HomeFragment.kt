package com.vitgames.weathertest.main.screen.fragments

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.MainActivity
import com.vitgames.weathertest.main.api.ApiWeather
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(R.layout.fragment_home) {

    var api: ApiWeather.ApiInterface? = null
    var todayWeather: TextView? = null

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        api = ApiWeather().getClient()?.create(ApiWeather.ApiInterface::class.java)
        return inflater.inflate(R.layout.fragment_home, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        todayWeather = view.findViewById(R.id.weatherToday)
        viewModel.progressLiveData.observe(this.viewLifecycleOwner) { success ->
            if (success.not()) {
                Toast.makeText(requireContext(), "LiveData false", Toast.LENGTH_SHORT).show()
            } else {
                todayWeather?.text = viewModel.weatherData
            }
        }
        super.onViewCreated(view, savedInstanceState)
    }


    fun getWeather(Lat: Double, Lon: Double) {
        viewModel.weatherResponse(Lat, Lon)
        todayWeather?.text = viewModel.weatherData
    }
}