package com.trilogy.mathlearning

import android.app.Application
import com.google.firebase.FirebaseApp
import com.trilogy.mathlearning.utils.SharedPreferencesReManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        SharedPreferencesReManager.init(applicationContext)
    }
}