package com.example.hocky

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class event_entry_Fragment : Fragment() {

    private lateinit var dbHelper: EventsDatabaseHelper  // Use EventsDatabaseHelper here
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventAdapter: EventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_event_entry_, container, false)

        dbHelper = EventsDatabaseHelper(requireContext())  // Instantiate EventsDatabaseHelper

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        eventAdapter = EventAdapter(eventList) { selectedEvent ->
            showEditDeleteDialog(selectedEvent)
        }

        recyclerView.adapter = eventAdapter

        val addEventButton = view.findViewById<FloatingActionButton>(R.id.add_event_button)
        addEventButton.setOnClickListener {
            showAddEventDialog()
        }

        loadEventsFromDb()  // Load events at start

        return view
    }

    private fun loadEventsFromDb() {
        val events = dbHelper.getAllEvents()  // Call on instance, not class
        eventAdapter.updateEvents(events)
    }

    private fun showAddEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.event_name_input)
        val dateInput = dialogView.findViewById<EditText>(R.id.event_date_input)
        val locationInput = dialogView.findViewById<EditText>(R.id.event_location_input)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = "🎫 ${nameInput.text.toString().trim()}"
                val date = "📅 ${dateInput.text.toString().trim()}"
                val location = "📍 ${locationInput.text.toString().trim()}"


                if (name.isNotEmpty() && date.isNotEmpty() && location.isNotEmpty()) {
                    val insertedId = dbHelper.insertEvent(name, date, location)
                    if (insertedId > 0) {
                        Toast.makeText(context, "Event Added!", Toast.LENGTH_SHORT).show()
                        loadEventsFromDb()  // Refresh list
                    } else {
                        Toast.makeText(context, "Failed to add event", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showEditDeleteDialog(event: Event) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_event, null)
        val nameInput = dialogView.findViewById<EditText>(R.id.event_name_input)
        val dateInput = dialogView.findViewById<EditText>(R.id.event_date_input)
        val locationInput = dialogView.findViewById<EditText>(R.id.event_location_input)

        // Remove emoji prefix for editing
        nameInput.setText(event.name.removePrefix("🎫 ").trim())
        dateInput.setText(event.date.removePrefix("📅 ").trim())
        locationInput.setText(event.location.removePrefix("📍 ").trim())

        AlertDialog.Builder(requireContext())
            .setTitle("Edit or Delete Event")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val updatedName = "🎫 ${nameInput.text.toString().trim()}"
                val updatedDate = "📅 ${dateInput.text.toString().trim()}"
                val updatedLocation = "📍 ${locationInput.text.toString().trim()}"

                dbHelper.updateEvent(event, updatedName, updatedDate, updatedLocation)
                Toast.makeText(context, "Event Updated", Toast.LENGTH_SHORT).show()
                loadEventsFromDb()
            }
            .setNegativeButton("Delete") { _, _ ->
                dbHelper.deleteEvent(event)
                Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show()
                loadEventsFromDb()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }


}
