package com.example.myhockey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

class user_signup : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_auth, container, false)

        val signUpForm = view.findViewById<LinearLayout>(R.id.sign_up)
        val signInForm = view.findViewById<LinearLayout>(R.id.sign_in_form)
        val toggleToSignin = view.findViewById<TextView>(R.id.textView7)
        val toggleToSignup = view.findViewById<TextView>(R.id.toggleToSignup)
        val signupSkipButton = view.findViewById<TextView>(R.id.skipTextView1)
        val loginSkipButton = view.findViewById<TextView>(R.id.skipTextView2)

        // Load animations
        val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        val slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)

        toggleToSignin.setOnClickListener {
            signUpForm.startAnimation(slideDown)
            signUpForm.visibility = View.GONE

            signInForm.startAnimation(slideUp)
            signInForm.visibility = View.VISIBLE
        }

        toggleToSignup.setOnClickListener {
            signInForm.startAnimation(slideDown)
            signInForm.visibility = View.GONE

            signUpForm.startAnimation(slideUp)
            signUpForm.visibility = View.VISIBLE
        }

        // Skip button for Sign Up form
        signupSkipButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("showLandingPage", true)
                putExtra("showBottomNav", true)
            }
            startActivity(intent)
            activity?.finish()
            activity?.overridePendingTransition(R.anim.fade_in_slide_up, R.anim.fade_out)
        }

        // Skip button for Sign In form
        loginSkipButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                putExtra("showLandingPage", true)
                putExtra("showBottomNav", true)
            }
            startActivity(intent)
            activity?.finish()
            activity?.overridePendingTransition(R.anim.fade_in_slide_up, R.anim.fade_out)
        }

        return view
    }
}
