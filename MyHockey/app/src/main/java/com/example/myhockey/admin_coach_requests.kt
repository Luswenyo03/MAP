package com.example.myhockey

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class admin_coach_requests : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbRef: DatabaseReference
    private val coachRequests = mutableListOf<User>()
    private lateinit var adapter: CoachRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_admin_coach_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.requestsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        dbRef = FirebaseDatabase.getInstance().getReference("users")

        // Initialize adapter early with empty list
        adapter = CoachRequestAdapter(coachRequests,
            onApprove = { user, message ->
                dbRef.child(user.id).child("approved").setValue(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dbRef.child(user.id).child("adminMessage").setValue(message)
                        Toast.makeText(requireContext(), "${user.firstName} approved", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to approve ${user.firstName}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onDecline = { user, message ->
                dbRef.child(user.id).child("approved").setValue(false).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        dbRef.child(user.id).child("adminMessage").setValue(message)
                        Toast.makeText(requireContext(), "${user.firstName} declined", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Failed to decline ${user.firstName}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )

        recyclerView.adapter = adapter

        fetchCoachRequests()
    }

    private fun fetchCoachRequests() {
        dbRef.orderByChild("role").equalTo("coach")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    coachRequests.clear()
                    for (child in snapshot.children) {
                        val user = child.getValue(User::class.java)
                        if (user != null && !user.approved) {
                            coachRequests.add(user)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
