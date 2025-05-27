package com.example.hocky

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlayerAdapter(
    private var players: List<Player> = emptyList(),
    private val onItemClick: (Player) -> Unit = {} // Optional click listener
) : RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val positionView: TextView = itemView.findViewById(R.id.tvPosition)
        private val teamView: TextView = itemView.findViewById(R.id.tvTeam)
        private val jerseyView: TextView = itemView.findViewById(R.id.tvJerseyNumber) // New field

        fun bind(player: Player) {
            nameView.text = player.name
            positionView.text = player.position
            teamView.text = player.team
            jerseyView.text = "#${player.jerseyNumber}" // Format jersey number

            // Optional click handling
            itemView.setOnClickListener { onItemClick(player) }
        }
    }

    fun updateList(newPlayers: List<Player>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_player, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(players[position])
    }

    override fun getItemCount() = players.size
}