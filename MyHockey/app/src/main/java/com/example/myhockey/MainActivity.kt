package com.example.myhockey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myhockey.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val showLanding = intent.getBooleanExtra("showLandingPage", false)
            val fragment = if (showLanding) landing_page() else GetStartedFragment()

            supportFragmentManager.beginTransaction()
                .replace(binding.fragmentContainerView.id, fragment)
                .commit()
        }

        val showBottomNav = intent.getBooleanExtra("showBottomNav", false)
        if (showBottomNav) {
            binding.bottomNav.visibility = View.VISIBLE
        }

        // Setup bottom nav listener
        binding.bottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> landing_page()
                R.id.matches -> matches()
                R.id.News -> news_updates()
                R.id.stats -> stats()
                else -> landing_page()
            }
            replaceFragment(selectedFragment)
            true
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_slide_up,
                R.anim.fade_out,
                R.anim.fade_in_slide_up,
                R.anim.fade_out
            )
            .replace(binding.fragmentContainerView.id, fragment)

        transaction.runOnCommit {
            Handler(Looper.getMainLooper()).postDelayed({
                binding.bottomNav.visibility =
                    if (fragment is GetStartedFragment) View.GONE else View.VISIBLE
            }, 200)
        }

        transaction.commit()
    }
}
