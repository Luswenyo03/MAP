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
import com.example.myhockey.databinding.ActivityCoachBinding

class coach : AppCompatActivity() {

    private lateinit var binding: ActivityCoachBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCoachBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.coachToolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        binding.coachBottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.home -> coach_home_page()
                R.id.players -> coach_player_management()
                R.id.team -> coach_team_management()
                R.id.chat -> coach_chat()
                R.id.requests -> request_coach()
                else -> coach_home_page()
            }
            replaceFragment(selectedFragment)
            true
        }

        replaceFragment(coach_home_page())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_slide_up,
                R.anim.fade_out,
                R.anim.fade_in_slide_up,
                R.anim.fade_out
            )
            .replace(binding.fragmentContainerView.id, fragment)

        transaction.commit()
    }
}
