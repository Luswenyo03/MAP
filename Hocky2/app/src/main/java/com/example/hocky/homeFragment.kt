package com.example.hocky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class homeFragment : Fragment() {

    private lateinit var dbHelper: EventsDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize the DB helper
        dbHelper = EventsDatabaseHelper(requireContext())

        // Get the count of events from DB
        val eventsCount = dbHelper.getEventsCount()

        // Find the TextView and set the count
        val eventsCountTextView = view.findViewById<TextView>(R.id.events)
        eventsCountTextView.text = eventsCount.toString()

        return view
    }
}
