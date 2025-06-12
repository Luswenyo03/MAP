package com.example.myhockey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myhockey.databinding.ActivityAdminBinding
import com.example.myhockey.databinding.ActivityCoachBinding

class admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.adminBottomNavigationView.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> admin_home()
                R.id.news -> admin_news_report()
                R.id.matches -> admin_fixtures_updates()
                R.id.requests -> admin_coach_requests()
                else -> admin_home()
            }
            replaceFragment(selectedFragment)
            true
        }

        replaceFragment(admin_home() )
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_slide_up,
                R.anim.fade_out,
                R.anim.fade_in_slide_up,
                R.anim.fade_out
            )
            .replace(binding.fragmentContainerView3.id, fragment)

        transaction.commit()
    }
}
