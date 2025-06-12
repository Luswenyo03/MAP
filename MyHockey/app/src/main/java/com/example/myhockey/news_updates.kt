package com.example.myhockey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class news_updates : Fragment() {

    data class NewsItem(
        val imageUrl: String = "",
        val title: String = "",
        val date: String = "",
        val content: String = "",
        val timestamp: Long = 0
    )

    private lateinit var recyclerView: RecyclerView
    private val newsList = mutableListOf<NewsItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_news_updates, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.news_update)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ExpandableNewsAdapter(newsList)

        fetchNewsFromFirebase()
    }

    private fun fetchNewsFromFirebase() {
        val dbRef = FirebaseDatabase.getInstance().getReference("news")
        dbRef.orderByChild("timestamp").get().addOnSuccessListener { snapshot ->
            newsList.clear()
            for (child in snapshot.children) {
                val item = child.getValue(NewsItem::class.java)
                if (item != null) newsList.add(item)
            }
            recyclerView.adapter?.notifyDataSetChanged()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load news", Toast.LENGTH_SHORT).show()
        }
    }

    inner class ExpandableNewsAdapter(private val newsList: List<NewsItem>) :
        RecyclerView.Adapter<ExpandableNewsAdapter.ViewHolder>() {

        private val expandedPositions = mutableSetOf<Int>()

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.newsImage)
            val title: TextView = view.findViewById(R.id.newsTitle)
            val date: TextView = view.findViewById(R.id.newsDate)
            val content: TextView = view.findViewById(R.id.newsContent)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news_expandable, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount() = newsList.size

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val news = newsList[position]

            Glide.with(holder.image.context)
                .load(news.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)

            holder.title.text = news.title

            val dateFormat = SimpleDateFormat("EEE, dd MMM · hh:mm a", Locale.getDefault())
            holder.date.text = dateFormat.format(Date(news.timestamp))

            holder.content.text = news.content
            holder.content.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                if (expandedPositions.contains(position)) {
                    expandedPositions.remove(position)
                } else {
                    expandedPositions.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }
}
