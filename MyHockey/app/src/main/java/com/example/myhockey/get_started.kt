package com.example.myhockey

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class GetStartedFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_get_started, container, false)

        val button = view.findViewById<Button>(R.id.button)
        button.setOnClickListener {
            val intent = Intent(requireContext(),user_auth::class.java)
            //We had user_auth
            startActivity(intent)
        }

        return view
    }


}
