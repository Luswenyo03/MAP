package com.example.hocky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class GetStarted : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_get_started, container, false)

        // Find the button by ID
        val getStartedButton: MaterialButton = view.findViewById(R.id.get_started)

        // Set an onClickListener to navigate to the login fragment
        getStartedButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, SignUp()) // Replace with LoginFragment
                .addToBackStack(null) // Add to back stack so you can go back to the previous fragment
                .commit()
        }

        return view
    }
}
