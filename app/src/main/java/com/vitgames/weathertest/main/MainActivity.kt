package com.vitgames.weathertest.main

import android.Manifest
import android.annotation.SuppressLint
import android.provider.Settings
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
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


class MainActivity() : AppCompatActivity() {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var lastLocation: Location? = null
    private val permissionManager = PermissionManager(this)

    override fun onResume() {
        getLastLocation()
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val fiveDaysFragment = ForecastFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        setCurrentFragment(homeFragment)

        permissionManager.runLocationPermissionDialog(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        bottomNavigationView.setOnNavigationItemSelectedListener() {
            when (it.itemId) {
                R.id.currentWeatherFragment -> setCurrentFragment(homeFragment)
                R.id.futureListWeatherFragment -> setCurrentFragment(fiveDaysFragment)
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
    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionManager.runLocationPermissionDialog(this)
            return
        }
        fusedLocationClient?.lastLocation!!.addOnCompleteListener(this) { task ->
            if (task.isSuccessful && task.result != null) {
                lastLocation = task.result
                val txtView: TextView = findViewById<View>(R.id.latitude) as TextView
                //TODO(delete annotation when location put in Api )
                txtView.text =
                    lastLocation!!.latitude.toString() + ":" + lastLocation!!.longitude.toString()
                //TODO
            } else {
                showLocationAlertDialog()

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