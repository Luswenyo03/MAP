package com.example.myhockey

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class coach_home_page : Fragment() {

    private lateinit var noTeamLayout: View
    private lateinit var teamContainer: View
    private lateinit var teamNameDisplay: TextView
    private lateinit var teamLogo: ImageView
    private lateinit var registerTeamButton: Button
    private lateinit var viewTeamButton: Button

    private val db = FirebaseDatabase.getInstance().reference
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coach_home_page, container, false)

        // Initialize views
        noTeamLayout = view.findViewById(R.id.noTeamLayout)
        teamContainer = view.findViewById(R.id.teamContainer)
        teamNameDisplay = view.findViewById(R.id.teamNameDisplay)
        teamLogo = view.findViewById(R.id.teamLogo)
        registerTeamButton = view.findViewById(R.id.registerTeamButton)
        viewTeamButton = view.findViewById(R.id.viewTeamButton)

        // Check for team data
        checkTeamExistence()

        // Set click listener for register team button
        registerTeamButton.setOnClickListener {
            // Show the DialogSelectTeam
            val dialog = DialogSelectTeam { teamName, logoUrl ->
                // Callback when team is saved
                saveTeamToDatabase(teamName, logoUrl)
            }
            dialog.show(parentFragmentManager, "DialogSelectTeam")
        }

        // Set click listener for view team button
        viewTeamButton.setOnClickListener {
            // Navigate to team details fragment/activity
            // Example: findNavController().navigate(R.id.action_coachHome_to_teamDetails)
        }

        return view
    }

    private fun checkTeamExistence() {
        val userId = auth.currentUser?.uid ?: return

        // Query Realtime Database for a team associated with the user
        db.child("teams")
            .orderByChild("coachId")
            .equalTo(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) {
                        // No team found, show noTeamLayout
                        noTeamLayout.visibility = View.VISIBLE
                        teamContainer.visibility = View.GONE
                    } else {
                        // Team found, show teamContainer and populate data
                        noTeamLayout.visibility = View.GONE
                        teamContainer.visibility = View.VISIBLE

                        val team = snapshot.children.first().getValue(Team::class.java)
                        val teamName = team?.teamName ?: "Unnamed Team"
                        val teamLogoUrl = team?.logoUrl

                        // Set team name
                        teamNameDisplay.text = teamName

                        // Load team logo using Glide
                        teamLogoUrl?.let {
                            Glide.with(this@coach_home_page)
                                .load(it)
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.news_1)
                                .into(teamLogo)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle errors (e.g., show a toast or log the error)
                    noTeamLayout.visibility = View.VISIBLE
                    teamContainer.visibility = View.GONE
                }
            })
    }

    private fun saveTeamToDatabase(teamName: String, logoUrl: String?) {
        val userId = auth.currentUser?.uid ?: return
        val teamId = db.child("teams").push().key ?: return

        // Create team data
        val teamData = hashMapOf(
            "coachId" to userId,
            "teamName" to teamName,
            "logoUrl" to logoUrl
        )

        // Save to Realtime Database
        db.child("teams").child(teamId).setValue(teamData)
            .addOnSuccessListener {
                // Team saved, refresh UI
                checkTeamExistence()
            }
            .addOnFailureListener { exception ->
                // Handle error (e.g., show a toast)
            }
    }
}

// Data class for team
data class Team(
    val coachId: String? = null,
    val teamName: String? = null,
    val logoUrl: String? = null
)

class DialogSelectTeam(private val onTeamSaved: (String, String?) -> Unit) : DialogFragment() {

    private var selectedImageUri: Uri? = null

    // Activity result launcher for image picker
    private val imagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let { uri ->
                view?.findViewById<ImageView>(R.id.logoPreview)?.let { imageView ->
                    Glide.with(this).load(uri).into(imageView)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.register_team_dialog, container, false)

        val teamNameInput = view.findViewById<EditText>(R.id.teamNameInput)
        val logoPreview = view.findViewById<ImageView>(R.id.logoPreview)
        val selectLogoBtn = view.findViewById<Button>(R.id.selectLogoBtn)
        val saveTeamBtn = view.findViewById<Button>(R.id.saveTeamBtn)

        // Set click listener for selecting logo
        selectLogoBtn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePicker.launch(intent)
        }

        // Set click listener for saving team
        saveTeamBtn.setOnClickListener {
            val teamName = teamNameInput.text.toString().trim()
            if (teamName.isNotEmpty()) {
                if (selectedImageUri != null) {
                    // Upload image to Cloudinary
                    uploadImageToCloudinary(teamName) { logoUrl ->
                        onTeamSaved(teamName, logoUrl)
                        dismiss()
                    }
                } else {
                    // Save without logo
                    onTeamSaved(teamName, null)
                    dismiss()
                }
            }
        }

        return view
    }

    private fun uploadImageToCloudinary(teamName: String, callback: (String?) -> Unit) {
        val imageUri = selectedImageUri ?: return callback(null)

        MediaManager.get().upload(imageUri)
            .option("public_id", "team_logos/$teamName-${System.currentTimeMillis()}")
            .unsigned("unsigned_news")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // Optional: Show loading indicator
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    // Optional: Update progress
                }

                override fun onSuccess(requestId: String, resultData: Map<Any?, Any?>) {
                    val url = resultData["secure_url"] as? String
                    callback(url)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    callback(null)
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Handle reschedule (e.g., retry logic or log the error)
                    callback(null)
                }
            })
            .dispatch()
    }
}