package com.example.myhockey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CoachRequestAdapter(
    private val coachList: List<User>,
    private val onApprove: (User, String) -> Unit,
    private val onDecline: (User, String) -> Unit
) : RecyclerView.Adapter<CoachRequestAdapter.CoachViewHolder>() {

    inner class CoachViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textCoachName)
        val email: TextView = view.findViewById(R.id.textCoachEmail)
        val image: ImageView = view.findViewById(R.id.imageUploadedFile)
        val message: EditText = view.findViewById(R.id.editAdminMessage)
        val approveBtn: Button = view.findViewById(R.id.buttonApprove)
        val declineBtn: Button = view.findViewById(R.id.buttonDecline)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_coach_request, parent, false)
        return CoachViewHolder(view)
    }

    override fun onBindViewHolder(holder: CoachViewHolder, position: Int) {
        val coach = coachList[position]
        holder.name.text = "${coach.firstName} ${coach.lastName}"
        holder.email.text = coach.email

        // Show placeholder image if file is PDF, else load image URL
        if (coach.imageUrl?.endsWith(".pdf") == true) {
            holder.image.setImageResource(R.drawable.news_2)  // Your pdf placeholder icon
        } else {
            Glide.with(holder.image.context)
                .load(coach.imageUrl)
                .placeholder(R.drawable.news_1)
                .into(holder.image)
        }

        // On image click: open PDF viewer fragment or activity
        holder.image.setOnClickListener {
            val context = holder.itemView.context
            if (coach.imageUrl.isNullOrEmpty()) {
                // No file to show
                Toast.makeText(context, "No file to display", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Open PdfViewerFragment (replace with your actual fragment container id)
            val fragment = PdfViewerFragment.newInstance(coach.imageUrl!!)
            if (context is androidx.fragment.app.FragmentActivity) {
                context.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView3, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        holder.approveBtn.setOnClickListener {
            val msg = holder.message.text.toString()
            onApprove(coach, msg)
        }

        holder.declineBtn.setOnClickListener {
            val msg = holder.message.text.toString()
            onDecline(coach, msg)
        }
    }


    override fun getItemCount(): Int = coachList.size
}
