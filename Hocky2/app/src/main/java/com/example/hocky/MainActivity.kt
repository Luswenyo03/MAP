package com.example.hocky

import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Only set initial fragment if no existing state
        if (supportFragmentManager.findFragmentById(R.id.fragmentContainerView) == null) {
            val sharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE)
            val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

            if (isFirstTime) {
                showFragment(GetStarted(), addToBackStack = false)
                sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
            } else {
                showFragment(login(), addToBackStack = false)
            }
        }
    }

    // Improved fragment transaction method
    fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, fragment)
            if (addToBackStack) {
                addToBackStack(fragment::class.java.simpleName)
            }
            commit()
        }
    }
}
