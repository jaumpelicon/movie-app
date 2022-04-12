package com.example.movieapp

import android.app.Application
import com.example.movieapp.data.di.databaseModule
import com.example.movieapp.data.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AppApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AppApplication)
            modules(listOf(viewModelModule, databaseModule))
        }
    }
}