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

        // Check if it's the first time the app is launched
        val sharedPreferences = getSharedPreferences("appPreferences", MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean("isFirstTime", true)

        // If it's the first time, show the GetStartedFragment
        if (isFirstTime) {
            showFragment(GetStarted())

            // Set the flag to false after first-time launch
            sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        } else {
            // Show LoginFragment or SignUpFragment based on the condition
            showFragment(login())  // Example: Show LoginFragment
        }


    }

    // Utility function to replace the fragment in the container
    private fun showFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentContainerView, fragment)
        transaction.commit()
    }
}
