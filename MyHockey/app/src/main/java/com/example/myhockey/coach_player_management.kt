package com.example.myhockey

import android.Manifest
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class coach_player_management : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddPlayer: FloatingActionButton
    private lateinit var playersList: MutableList<Player>
    private lateinit var adapter: PlayerAdapter
    private var dialogView: View? = null // Store dialog view for image preview
    private var selectedImageUri: android.net.Uri? = null
    private lateinit var cloudinary: Cloudinary

    // Permission and image picker launchers
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            openImagePicker()
        } else {
            Toast.makeText(requireContext(), "Permission denied. Cannot select image.", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        uri?.let {
            selectedImageUri = it
            dialogView?.findViewById<ImageView>(R.id.playerImagePreview)?.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coach_player_management, container, false)

        recyclerView = view.findViewById(R.id.recycler_viewer)
        fabAddPlayer = view.findViewById(R.id.add_team_button)

        playersList = mutableListOf()
        adapter = PlayerAdapter(playersList)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Initialize Cloudinary (move credentials to backend in production)
        cloudinary = Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", "ddncw6btd",
                "api_key", "342284817272313",
                "api_secret", "eVUVtmRfdYULAghCG72iq2W5T6E"
            )
        )

        fabAddPlayer.setOnClickListener {
            showAddPlayerDialog()
        }

        loadPlayers()

        return view
    }


    private fun loadPlayers() {
        val currentCoachId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val playersRef = FirebaseDatabase.getInstance().reference.child("players")

        // Query players where coachId == currentCoachId
        val query = playersRef.orderByChild("coachId").equalTo(currentCoachId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                playersList.clear()
                for (playerSnapshot in snapshot.children) {
                    val player = playerSnapshot.getValue(Player::class.java)
                    player?.let { playersList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load players: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }


    private fun showAddPlayerDialog() {
        dialogView = layoutInflater.inflate(R.layout.dialog_add_player, null)
        val nameInput = dialogView?.findViewById<EditText>(R.id.playerNameInput)
        val positionInput = dialogView?.findViewById<EditText>(R.id.playerPositionInput)
        val idNumberInput = dialogView?.findViewById<EditText>(R.id.playerIdInput)
        val selectImageBtn = dialogView?.findViewById<Button>(R.id.selectImageBtn)
        val imagePreview = dialogView?.findViewById<ImageView>(R.id.playerImagePreview)

        selectedImageUri = null
        imagePreview?.setImageDrawable(null) // Clear preview

        selectImageBtn?.setOnClickListener {
            requestStoragePermission()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Player")
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = nameInput?.text.toString().trim()
                val position = positionInput?.text.toString().trim()
                val idNumber = idNumberInput?.text.toString().trim()

                if (name.isEmpty() || position.isEmpty() || idNumber.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (selectedImageUri == null) {
                    Toast.makeText(requireContext(), "Select an image", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                checkPlayerExistsAndUpload(name, position, idNumber, dialog)
            }
        }

        dialog.show()
    }

    private fun checkPlayerExistsAndUpload(name: String, position: String, idNumber: String, dialog: AlertDialog) {
        val allPlayerIdsRef = FirebaseDatabase.getInstance().reference.child("allPlayerIds").child(idNumber)

        allPlayerIdsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(
                        requireContext(),
                        "This player is already registered.",
                        Toast.LENGTH_LONG
                    ).show()
                    return
                }
                uploadImageAndSavePlayer(name, position, idNumber, dialog)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Check failed: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseDatabase", "Check player error: ${error.message}", error.toException())
            }
        })
    }

    private fun uploadImageAndSavePlayer(name: String, position: String, idNumber: String, dialog: AlertDialog) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputStream = selectedImageUri?.let { requireContext().contentResolver.openInputStream(it) }
                    ?: throw Exception("No image selected")
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val imageUrl = uploadResult["secure_url"] as? String ?: throw Exception("Failed to get image URL")

                withContext(Dispatchers.Main) {
                    savePlayer(name, position, idNumber, imageUrl)
                    dialog.dismiss()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("CloudinaryError", "Upload error: ${e.message}", e)
                }
            }
        }
    }

    private fun savePlayer(name: String, position: String, idNumber: String, imageUrl: String) {
        val coachId = FirebaseAuth.getInstance().currentUser?.uid
        if (coachId == null) {
            Toast.makeText(requireContext(), "User not logged in. Cannot save player.", Toast.LENGTH_LONG).show()
            Log.e("FirebaseDatabase", "No logged-in user found.")
            return
        }

        val playersRef = FirebaseDatabase.getInstance().reference.child("players")
        val playerId = playersRef.push().key
        if (playerId == null) {
            Toast.makeText(requireContext(), "Failed to generate player ID", Toast.LENGTH_LONG).show()
            Log.e("FirebaseDatabase", "Failed to generate player ID")
            return
        }

        val player = Player(
            id = playerId,
            name = name,
            position = position,
            imageUrl = imageUrl,
            coachId = coachId,
            idNumber = idNumber
        )

        val playerPath = "players/$playerId"
        val globalPath = "allPlayerIds/$idNumber"

        val updates = hashMapOf<String, Any>(
            playerPath to player,
            globalPath to true
        )

        FirebaseDatabase.getInstance().reference.updateChildren(updates)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Player added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add player: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseDatabase", "Save player error: ${e.message}", e)
            }
    }

    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openImagePicker() {
        pickImageLauncher.launch("image/*")
    }
}

data class Player(
    var id: String? = null,
    var name: String? = null,
    var position: String? = null,
    var imageUrl: String? = null,
    var coachId: String? = null,
    var idNumber: String? = null
)

class PlayerAdapter(private val players: List<Player>) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {

    inner class PlayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val playerImage: ImageView = itemView.findViewById(R.id.playerImage)
        val playerName: TextView = itemView.findViewById(R.id.playerName)
        val playerPosition: TextView = itemView.findViewById(R.id.playerPosition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.player_item, parent, false)
        return PlayerViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        val player = players[position]
        holder.playerName.text = player.name ?: "Unknown"
        holder.playerPosition.text = player.position ?: "Unknown"

        // If you have an image URL, load it using Glide or Picasso, else use a placeholder
        val context = holder.itemView.context
        if (!player.imageUrl.isNullOrEmpty()) {
            Glide.with(context)
                .load(player.imageUrl)
                .placeholder(R.drawable.person_add_24dp_e8eaed_fill0_wght400_grad0_opsz24) // your placeholder
                .into(holder.playerImage)
        } else {
            holder.playerImage.setImageResource(R.drawable.person_add_24dp_e8eaed_fill0_wght400_grad0_opsz24)
        }
    }

    override fun getItemCount(): Int = players.size
}
