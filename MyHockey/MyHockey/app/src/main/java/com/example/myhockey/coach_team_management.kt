package com.example.myhockey

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class coach_team_management : Fragment() {

    private lateinit var formationRecyclerView: RecyclerView
    private lateinit var formationContainer: ConstraintLayout

    // Sample formations with player positions (x%, y% on the field layout)
    private val formations = listOf(
        Formation(
            name = "4-4-2",
            positions = listOf(
                PlayerPosition("GK", 41f, 85f),
                PlayerPosition("LB", 7f, 67f),
                PlayerPosition("LCB", 27f, 71f),
                PlayerPosition("RCB", 56f, 71f),
                PlayerPosition("RB", 75f, 67f),
                PlayerPosition("LM", 7f, 45f),
                PlayerPosition("LCM", 27f, 50f),
                PlayerPosition("RCM", 56f, 50f),
                PlayerPosition("RM", 75f, 45f),
                PlayerPosition("ST", 27f, 23f),
                PlayerPosition("ST", 56f, 23f)
            )
        ),
        Formation(
            name = "4-3-3",
            positions = listOf(
                PlayerPosition("GK", 41f, 85f),
                PlayerPosition("LB", 7f, 67f),
                PlayerPosition("LCB", 27f, 63f),
                PlayerPosition("RCB", 56f, 63f),
                PlayerPosition("RB", 75f, 67f),
                PlayerPosition("LCM", 7f, 50f),
                PlayerPosition("CM", 41f, 45f),
                PlayerPosition("RCM", 75f, 50f),
                PlayerPosition("LW", 7f, 30f),
                PlayerPosition("ST", 41f, 23f),
                PlayerPosition("RW", 75f, 30f)
            )
        )
        // Add more formations here...
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_coach_team_management, container, false)

        formationRecyclerView = root.findViewById(R.id.formationRecyclerView)
        formationContainer = root.findViewById(R.id.formationContainer)

        setupFormationRecyclerView()

        // Show first formation by default
        showFormation(formations[0])

        return root
    }

    private fun setupFormationRecyclerView() {
        formationRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = FormationAdapter(formations) { formation ->
            // When a formation is clicked, update the field view
            showFormation(formation)
        }

        formationRecyclerView.adapter = adapter
    }

    private fun showFormation(formation: Formation) {
        formationContainer.removeAllViews()

        val containerWidth = formationContainer.width
        val containerHeight = formationContainer.height

        if (containerWidth == 0 || containerHeight == 0) {
            formationContainer.post {
                showFormation(formation)
            }
            return
        }

        for (pos in formation.positions) {
            val playerView = TextView(requireContext()).apply {
                text = pos.positionName
                setBackgroundColor(Color.parseColor("#AA2196F3"))
                setTextColor(ContextCompat.getColor(context, R.color.md_theme_onBackground))

                setPadding(12)
                textSize = 12f
                id = View.generateViewId()
                background = requireContext().getDrawable(R.drawable.recycle_view)

                tooltipText = "Position: ${pos.positionName}"

                setOnClickListener {
                    Toast.makeText(requireContext(), "Clicked: ${pos.positionName}", Toast.LENGTH_SHORT).show()
                }
            }

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            playerView.layoutParams = layoutParams
            formationContainer.addView(playerView)

            // Calculate position in pixels
            val x = (containerWidth * (pos.xPercent / 100f)).toInt()
            val y = (containerHeight * (pos.yPercent / 100f)).toInt()

            // Apply constraints to position the player
            val constraintSet = androidx.constraintlayout.widget.ConstraintSet()
            constraintSet.clone(formationContainer)

            constraintSet.connect(playerView.id, ConstraintLayout.LayoutParams.LEFT, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintLayout.LayoutParams.LEFT)
            constraintSet.connect(playerView.id, ConstraintLayout.LayoutParams.TOP, ConstraintLayout.LayoutParams.PARENT_ID, ConstraintLayout.LayoutParams.TOP)

            constraintSet.setMargin(playerView.id, ConstraintLayout.LayoutParams.LEFT, x)
            constraintSet.setMargin(playerView.id, ConstraintLayout.LayoutParams.TOP, y)

            constraintSet.applyTo(formationContainer)
        }


    }



    // Data classes for formation and positions
    data class Formation(val name: String, val positions: List<PlayerPosition>)
    data class PlayerPosition(val positionName: String, val xPercent: Float, val yPercent: Float)
}
