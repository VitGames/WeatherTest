package com.vitgames.weathertest.main


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
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
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            if (p0 == null) {
                return
            } else {
                currentLocation = p0.lastLocation
                Log.e("LOCATION UPDATE", "Location restored")
            }
        }

        override fun onLocationAvailability(p0: LocationAvailability) {
            super.onLocationAvailability(p0)
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    override fun onResume() {
        permissionManager.checkInternetConnection()
        if (checkLocationEnabled()) {
            //currentLocation = getLocation()
            startLocationUpdates()
        }
        super.onResume()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create()
        locationRequest?.interval = 4000
        locationRequest?.fastestInterval = 2000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        permissionManager.runLocationPermissionDialog(this)
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

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        val request: LocationSettingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!).build()
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val locationSettingsResponseTask: Task<LocationSettingsResponse> =
            client.checkLocationSettings(request)
        locationSettingsResponseTask.addOnSuccessListener {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest!!,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        locationSettingsResponseTask.addOnFailureListener {
            Log.e("LOCATION UPDATE", "Location null")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopLocationUpdates() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest!!,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getLocation(): Location? {
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
            if (location != null) {
                currentLocation = location
            } else {
                Log.e("LOCATION START", "Location null")
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
            Handler().postDelayed(
                {
                    startLocationUpdates()
                },
                3000
            )
        }
        return currentLocation
    }
}