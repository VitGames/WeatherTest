package com.vitgames.weathertest.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.screen.fragments.ForecastFragment
import com.vitgames.weathertest.main.screen.fragments.HomeFragment
import com.vitgames.weathertest.main.support.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.withContext


class MainActivity() : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var location: Location? = null
    private val permissionManager = PermissionManager(this)
    private val homeFragment = HomeFragment()

    override fun onResume() {
        getLocation()
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val forecastFragment = ForecastFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setCurrentFragment(homeFragment)
        permissionManager.runLocationPermissionDialog(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        bottomNavigationView.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.currentWeatherFragment -> setCurrentFragment(homeFragment)
                R.id.futureListWeatherFragment -> setCurrentFragment(forecastFragment)
            }
            true
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionManager.runLocationPermissionDialog(this)
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                location = task.result
                val lat = location!!.latitude
                val lon = location!!.longitude
                if (permissionManager.checkInternetConnection()) {
                    homeFragment.getWeather(lat, lon)
                }
            } else {
                if (location == null) {
                    showLocationAlertDialog()
                }
            }
        }
    }

    private fun showLocationAlertDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location error:")
            .setMessage("Please, turn on location in settings")
            .setNegativeButton("Open settings") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.show()
    }
}