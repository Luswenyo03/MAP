package com.example.hocky  // Fixed package name

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar

class PlayerRegistrationDialog(
    private val onSave: (Player) -> Unit
) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_management, container, false)

        // Set up date picker for DOB
        view.findViewById<TextInputEditText>(R.id.playerDobEditText).setOnClickListener {
            showDatePickerDialog(view)
        }

        // Set up position spinner
        val spinner = view.findViewById<Spinner>(R.id.positionSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.positions_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        // Handle registration button
        view.findViewById<Button>(R.id.registerPlayerButton).setOnClickListener {
            val name = view.findViewById<TextInputEditText>(R.id.playerNameEditText).text.toString()
            val dob = view.findViewById<TextInputEditText>(R.id.playerDobEditText).text.toString()
            val jerseyText = view.findViewById<TextInputEditText>(R.id.playerJerseyEditText).text.toString()
            val jerseyNumber = jerseyText.toIntOrNull() ?: 0
            val contact = view.findViewById<TextInputEditText>(R.id.playerContactEditText).text.toString()
            val email = view.findViewById<TextInputEditText>(R.id.playerEmailEditText).text.toString()
            val team = view.findViewById<TextInputEditText>(R.id.playerTeamEditText).text.toString()

            val gender = when (view.findViewById<RadioGroup>(R.id.genderRadioGroup).checkedRadioButtonId) {
                R.id.genderMale -> "Male"
                R.id.genderFemale -> "Female"
                else -> "Other"
            }

            val position = spinner.selectedItem.toString()

            // Create Player object with proper parameters
            val player = Player(
                id = 0,  // Using default value for new entries
                name = name,
                position = position,
                team = team,
                jerseyNumber = jerseyNumber,
                dob = dob.ifBlank { null },
                contact = contact.ifBlank { null },
                email = email.ifBlank { null },
                gender = gender.ifBlank { null }
            )

            if (validateInputs(player)) {
                onSave(player)
                dismiss()
            }
        }

        return view
    }

    private fun validateInputs(player: Player): Boolean {
        var isValid = true

        // Add validation logic here (e.g., check required fields)
        if (player.name.isBlank()) {
            view?.findViewById<TextInputEditText>(R.id.playerNameEditText)?.error = "Name required"
            isValid = false
        }

        if (player.jerseyNumber <= 0) {
            view?.findViewById<TextInputEditText>(R.id.playerJerseyEditText)?.error = "Invalid number"
            isValid = false
        }

        return isValid
    }

    private fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = "${month + 1}/$day/$year"
                view.findViewById<TextInputEditText>(R.id.playerDobEditText).setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}