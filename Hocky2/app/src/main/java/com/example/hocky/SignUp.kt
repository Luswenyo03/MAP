package com.example.hocky

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.hocky.DatabaseHelper.Companion.COLUMN_EMAIL
import com.example.hocky.DatabaseHelper.Companion.COLUMN_ID_NUMBER
import com.example.hocky.DatabaseHelper.Companion.TABLE_NAME
import com.google.android.material.button.MaterialButton

class SignUp : Fragment(R.layout.fragment_sign_up) {

    private lateinit var fullNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var idNumberEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var signUpButton: MaterialButton

    private fun clearFields() {
        fullNameEditText.text.clear()
        emailEditText.text.clear()
        idNumberEditText.text.clear()
        passwordEditText.text.clear()
        confirmPasswordEditText.text.clear()
    }

    // Declare DatabaseHelper
    private lateinit var dbHelper: DatabaseHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the views
        fullNameEditText = view.findViewById(R.id.editTextText2)
        emailEditText = view.findViewById(R.id.editTextTextEmailAddress)
        idNumberEditText = view.findViewById(R.id.editTextNumber)
        passwordEditText = view.findViewById(R.id.editTextTextPassword)
        confirmPasswordEditText = view.findViewById(R.id.editTextTextPassword2)
        signUpButton = view.findViewById(R.id.button2)

        // Initialize DatabaseHelper
        dbHelper = DatabaseHelper(requireContext())

        // Set the button click listener
        signUpButton.setOnClickListener {
            validateInputs()
        }
    }

    // Move this to DatabaseHelper ideally, but here for now
    fun userExists(email: String, idNumber: String): Boolean {
        val db = dbHelper.readableDatabase
        val query = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_EMAIL = ? OR $COLUMN_ID_NUMBER = ?"
        val cursor = db.rawQuery(query, arrayOf(email, idNumber))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // Validation function
    private fun validateInputs() {
        val fullName = fullNameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val idNumber = idNumberEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        var isValid = true

        // Check if any field is empty
        if (fullName.isEmpty()) {
            fullNameEditText.error = "Full name is required"
            isValid = false
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email is required"
            isValid = false
        }

        if (idNumber.isEmpty()) {
            idNumberEditText.error = "ID number is required"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password is required"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Please confirm your password"
            isValid = false
        }

        // Check if passwords match
        if (password.isNotEmpty() && confirmPassword.isNotEmpty() && password != confirmPassword) {
            confirmPasswordEditText.error = "Passwords do not match"
            isValid = false
        }


// If all fields are valid, continue
        if (isValid) {
            // Check if user already exists
            if (userExists(email, idNumber)) {
                Toast.makeText(requireContext(), "User with this Email or ID Number already exists", Toast.LENGTH_SHORT).show()

                // Redirect to SignInFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, login()) // Replace with SignInFragment
                    .addToBackStack(null) // Add to back stack to allow back navigation
                    .commit()

                clearFields()  // Clear fields after redirect
                return
            }

            // Insert the user into the database
            val result = dbHelper.insertUser(fullName, email, idNumber, password)

            if (result != -1L) {
                Toast.makeText(requireContext(), "Sign Up Successful, please log in", Toast.LENGTH_SHORT).show()
                // Redirect to SignInFragment
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, login()) // Replace with SignInFragment
                    .addToBackStack(null) // Add to back stack to allow back navigation
                    .commit()

                clearFields()  // Clear fields after redirect
                return
            } else {
                Toast.makeText(requireContext(), "Error creating account, try again", Toast.LENGTH_SHORT).show()
            }
        }



}

}
