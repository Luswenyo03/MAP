package com.example.hocky

import TeamAdapter
import TeamDatabaseHelper
import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class team_reg_Fragment : Fragment() {
    private lateinit var databaseHelper: TeamDatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var teamAdapter: TeamAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_team_reg_, container, false)

        databaseHelper = TeamDatabaseHelper(requireContext())
        recyclerView = view.findViewById(R.id.recycler_viewer)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadTeams()

        view.findViewById<View>(R.id.add_team_button).setOnClickListener {
            showAddTeamDialog()
        }

        return view
    }

    private fun loadTeams() {
        val teamList = databaseHelper.getAllTeams()
        teamAdapter = TeamAdapter(teamList)
        recyclerView.adapter = teamAdapter
    }

    private fun showAddTeamDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_team, null)
        val teamNameInput = dialogView.findViewById<EditText>(R.id.team_name_input)
        val coachNameInput = dialogView.findViewById<EditText>(R.id.coach_name_input)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Team")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val teamName = teamNameInput.text.toString().trim()
                val coachName = coachNameInput.text.toString().trim()

                if (teamName.isNotEmpty() && coachName.isNotEmpty()) {
                    val success = insertTeamToDb(teamName, coachName)
                    if (success) {
                        Toast.makeText(context, "Team added", Toast.LENGTH_SHORT).show()
                        loadTeams()
                    } else {
                        Toast.makeText(context, "Failed to add team", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun insertTeamToDb(teamName: String, coachName: String): Boolean {
        return try {
            val values = ContentValues().apply {
                put(TeamDatabaseHelper.COLUMN_TEAM_NAME, teamName)
                put(TeamDatabaseHelper.COLUMN_COACH_NAME, coachName)
            }
            val db = databaseHelper.writableDatabase
            val result = db.insert(TeamDatabaseHelper.TABLE_TEAMS, null, values)
            db.close()
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
