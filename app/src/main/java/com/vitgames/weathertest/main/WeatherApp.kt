package com.vitgames.weathertest.main

import android.app.Application
import com.vitgames.weathertest.main.screen.fragments.HomeViewModel
import com.vitgames.weathertest.main.support.PermissionManager
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class WeatherApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@WeatherApp)
            modules(listOf(viewModels))
        }
    }
    private val viewModels = module {
        single { PermissionManager(get()) }
        viewModel {HomeViewModel(get())}
    }
}