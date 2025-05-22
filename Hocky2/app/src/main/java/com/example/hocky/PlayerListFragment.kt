package com.example.hocky

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlayerAdapter
    private lateinit var dbHelper: PlayerDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_player_list, container, false)
        dbHelper = PlayerDatabaseHelper(requireContext())

        initializeRecyclerView(view)
        setupAddPlayerButton(view)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadPlayers()
    }

    private fun initializeRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.player_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlayerAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupAddPlayerButton(view: View) {
        view.findViewById<FloatingActionButton>(R.id.add_player_button).setOnClickListener {
            showPlayerRegistrationDialog()
        }
    }

    private fun showPlayerRegistrationDialog() {
        PlayerRegistrationDialog { player ->
            dbHelper.addPlayer(player)
            loadPlayers()
        }.show(parentFragmentManager, "PlayerRegistrationDialog")
    }

    fun loadPlayers() {
        val players = dbHelper.getAllPlayers()
        adapter.updateList(players)
    }
}