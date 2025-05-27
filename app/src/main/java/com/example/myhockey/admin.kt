package com.example.myhockey

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.myhockey.databinding.ActivityAdminBinding

class admin : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.adminBottomNav.setOnItemSelectedListener { item ->
            val selectedFragment = when (item.itemId) {
                R.id.nav_home -> admin_home_page()
                R.id.nav_matches -> admin_match_management()
                R.id.nav_news -> admin_news_management()
                R.id.nav_livescore -> Livescore_management()
                R.id.nav_stats -> admin_stats()
                else -> admin_home_page()
            }
            replaceFragment(selectedFragment)
            true
        }

        replaceFragment(admin_home_page())
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_slide_up,
                R.anim.fade_out,
                R.anim.fade_in_slide_up,
                R.anim.fade_out
            )
            .replace(R.id.admin_fragment_container, fragment)
        transaction.commit()
    }
}
