package com.example.myhockey

import android.app.Application
import com.cloudinary.android.MediaManager

class MyHockeyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val config: HashMap<String, String> = HashMap()
        config["cloud_name"] = "your_cloud_name"           // Replace with your actual Cloudinary cloud name
        config["api_key"] = "your_api_key"                 // Replace with your API key
        config["api_secret"] = "your_api_secret"           // Optional if using unsigned upload

        MediaManager.init(this, config)
    }
}
