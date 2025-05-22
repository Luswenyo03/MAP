
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hocky.PlayerAdapter
import com.example.hocky.PlayerDatabaseHelper
import com.example.hocky.R
import com.example.hocky.playerManagementFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlayerListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlayerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_player_list, container, false)

        // Initialize RecyclerView with empty list
        recyclerView = view.findViewById(R.id.playerRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = PlayerAdapter()
        recyclerView.adapter = adapter

        loadPlayers()

        view.findViewById<FloatingActionButton>(R.id.fabAddPlayer).setOnClickListener {
            // Shortest working version
            findNavController().navigate(R.id.action_to_playerManagement)

        }

        return view
    }

    private fun loadPlayers() {
        val dbHelper = PlayerDatabaseHelper(requireContext())
        val players = dbHelper.getAllPlayers()
        adapter.updateList(players)
    }
}