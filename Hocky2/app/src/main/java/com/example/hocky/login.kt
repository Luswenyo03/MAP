package com.example.hocky

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton

class login : Fragment() {

    private lateinit var dbHelper: DatabaseHelper


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        dbHelper = DatabaseHelper(requireContext())

        val emailEditText: EditText = view.findViewById(R.id.editTextText2)
        val passwordEditText: EditText = view.findViewById(R.id.editTextTextPassword)
        val loginButton: MaterialButton = view.findViewById(R.id.login)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val isValid = dbHelper.checkUserCredentials(email, password)

                if (isValid) {
                    Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), dashboard::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "Incorrect credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }


        return view
    }
}
