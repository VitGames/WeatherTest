package com.vitgames.weathertest.main


import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vitgames.weathertest.R
import com.vitgames.weathertest.main.screen.fragments.FiveDaysFragment
import com.vitgames.weathertest.main.screen.fragments.HomeFragment
import com.vitgames.weathertest.main.support.PermissionManager

class MainActivity() : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissionManager = PermissionManager(this)
        val homeFragment = HomeFragment()
        val fiveDaysFragment = FiveDaysFragment()
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)

        setCurrentFragment(homeFragment)

        permissionManager.runLocationPermissionDialog(this)

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

}