package com.example.myhockey


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FormationAdapter(
    private val formations: List<coach_team_management.Formation>,
    private val onFormationClick: (coach_team_management.Formation) -> Unit
) : RecyclerView.Adapter<FormationAdapter.FormationViewHolder>() {

    // Track selected position to highlight the selected formation
    private var selectedPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FormationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return FormationViewHolder(view)
    }

    override fun getItemCount(): Int = formations.size

    override fun onBindViewHolder(holder: FormationViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val formation = formations[position]
        holder.bind(formation, position == selectedPosition)

        holder.itemView.setOnClickListener {
            val previousSelected = selectedPosition
            selectedPosition = position
            notifyItemChanged(previousSelected)
            notifyItemChanged(selectedPosition)
            onFormationClick(formation)
        }
    }

    inner class FormationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val formationNameTextView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(formation: coach_team_management.Formation, isSelected: Boolean) {
            formationNameTextView.text = formation.name
            formationNameTextView.textSize = 18f
            formationNameTextView.setPadding(32, 16, 32, 16)

            // Highlight selected item
            if (isSelected) {
                formationNameTextView.setBackgroundColor(Color.parseColor("#2196F3")) // Blue background
                formationNameTextView.setTextColor(Color.WHITE)
            } else {
                formationNameTextView.setBackgroundColor(Color.TRANSPARENT)
                formationNameTextView.setTextColor(Color.BLACK)
            }
        }
    }
}
