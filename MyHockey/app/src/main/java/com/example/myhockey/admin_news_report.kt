package com.example.myhockey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class admin_news_report : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_news_report, container, false)

        val addNewsButton = view.findViewById<FloatingActionButton>(R.id.addNewsButton)

        addNewsButton?.setOnClickListener {
            // Show the Add News dialog
            val dialog = AddNewsDialogFragment()
            dialog.show(childFragmentManager, "AddNewsDialog")
        }

        return view
    }
}