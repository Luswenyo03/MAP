package com.example.myhockey


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AddNewsDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(requireContext())
        val view = inflater.inflate(R.layout.dialog_add_news, null)

        // Optional: You can initialize views here, e.g.
        // val pickImageBtn = view.findViewById<Button>(R.id.btnPickImage)

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
    }
}
