package com.vitgames.weathertest.main


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
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
import com.vitgames.weathertest.main.support.Locator
import com.vitgames.weathertest.main.support.PermissionManager

//TODO broadcast receiver network & location
class MainActivity : AppCompatActivity(), Locator {
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private val permissionManager = PermissionManager(this)
    private val homeFragment = HomeFragment()
    private val forecastFragment = ForecastFragment()
    var currentLocation: Location? = null


    override fun onResume() {
        permissionManager.checkInternetConnection()
        if (checkLocationEnabled()) {
            currentLocation = getLocation()
        }
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        permissionManager.runLocationPermissionDialog(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        permissionManager.checkInternetConnection()
        currentLocation = getLocation()
        if (checkLocationEnabled()) {
            getLocation()
        }
        setCurrentFragment(homeFragment)
        bottomNavigationView.setOnNavigationItemSelectedListener {
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

    private fun getLocation(): Location? {
        if (checkLocationEnabled()) {
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
            fusedLocationClient!!.lastLocation.addOnFailureListener(this) {
                Toast.makeText(this, "Location error", Toast.LENGTH_SHORT)
                    .show()
            }
            fusedLocationClient!!.lastLocation.addOnSuccessListener { location: Location? ->
                val listenerLocation: Location? = location
                if (listenerLocation != null) {
                    currentLocation = listenerLocation
                } else {
                    Toast.makeText(this, "Location null", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return currentLocation
    }

    private fun checkLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showLocationAlertDialog()
            return false
        }
        return true
    }

    private fun showLocationAlertDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Location error:")
            .setMessage("Please, turn on location in settings")
            .setNegativeButton("Open settings") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }.show()
    }

     override fun getLocationDouble(): Location? {
        if (currentLocation == null) {
            currentLocation = getLocation()
        }
        return currentLocation
    }
}