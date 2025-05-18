// PlayerAdapter.kt
package com.example.hocky
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hocky.Player
import com.example.hocky.R

class PlayerAdapter(private var players: List<Player> = emptyList()) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.findViewById(R.id.tvPlayerName)
        private val positionView: TextView = itemView.findViewById(R.id.tvPosition)
        private val teamView: TextView = itemView.findViewById(R.id.tvTeam)

        fun bind(player: Player) {
            nameView.text = player.name
            positionView.text = player.position
            teamView.text = player.team
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