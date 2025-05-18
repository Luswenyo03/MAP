package com.example.hocky

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat



class playerManagementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_management, container, false)

        // Get references to all views
        val nameField = view.findViewById<TextInputEditText>(R.id.playerNameEditText)
        val dobField = view.findViewById<TextInputEditText>(R.id.playerDobEditText)
        val jerseyField = view.findViewById<TextInputEditText>(R.id.playerJerseyEditText)
        val contactField = view.findViewById<TextInputEditText>(R.id.playerContactEditText)
        val emailField = view.findViewById<TextInputEditText>(R.id.playerEmailEditText)
        val teamField = view.findViewById<TextInputEditText>(R.id.playerTeamEditText)
        val genderGroup = view.findViewById<RadioGroup>(R.id.genderRadioGroup)
        val positionSpinner = view.findViewById<Spinner>(R.id.positionSpinner)
        val registerButton = view.findViewById<Button>(R.id.registerPlayerButton)

        // Populate position spinner
        val positions = listOf(
            "Goalkeeper", "Right Fullback", "Left Fullback", "Center Halfback",
            "Right Halfback", "Left Halfback", "Right Wing", "Left Wing",
            "Center Forward", "Inside Right", "Inside Left"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, positions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        positionSpinner.adapter = adapter

        // Date picker for DOB
        dobField.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date of Birth")
                .build()

            datePicker.show(parentFragmentManager, "dob_picker")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = Date(selection)
                dobField.setText(sdf.format(date))
            }
        }

        // Register button logic
        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val dob = dobField.text.toString().trim()
            val jersey = jerseyField.text.toString().trim()
            val contact = contactField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val team = teamField.text.toString().trim()
            val selectedGenderId = genderGroup.checkedRadioButtonId
            val gender = if (selectedGenderId != -1)
                view.findViewById<RadioButton>(selectedGenderId).text.toString()
            else
                ""

            val position = positionSpinner.selectedItem.toString()

            // Validate required fields
            if (name.isEmpty() || dob.isEmpty() || jersey.isEmpty() || contact.isEmpty() || team.isEmpty() || gender.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate email if entered
            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "Enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Save player to database here

            // Confirmation
            Toast.makeText(requireContext(), "Player $name registered successfully!", Toast.LENGTH_LONG).show()

            // Clear fields
            nameField.text?.clear()
            dobField.text?.clear()
            jerseyField.text?.clear()
            contactField.text?.clear()
            emailField.text?.clear()
            teamField.text?.clear()
            genderGroup.clearCheck()
            positionSpinner.setSelection(0)


            val player = Player(
                name = name,
                dob = dob,
                jerseyNumber = jersey,
                contact = contact,
                email = if (email.isEmpty()) null else email,
                team = team,
                gender = gender,
                position = position
            )

            val dbHelper = PlayerDatabaseHelper(requireContext())
            if (dbHelper.insertPlayer(player)) {
                Toast.makeText(requireContext(), "Player saved!", Toast.LENGTH_SHORT).show()
                // Clear fields or navigate back
            } else {
                Toast.makeText(requireContext(), "Error saving player", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
