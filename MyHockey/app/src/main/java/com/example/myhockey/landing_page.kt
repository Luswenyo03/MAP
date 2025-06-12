package com.example.myhockey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class landing_page : Fragment() {

    private lateinit var viewPager: ViewPager2
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = viewPager.adapter?.itemCount ?: 0
            if (itemCount == 0) return

            val nextItem = (viewPager.currentItem + 1) % itemCount
            viewPager.currentItem = nextItem

            // Schedule next scroll after 3 seconds
            autoScrollHandler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_landing_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBar = view.findViewById<CardView>(R.id.topBar)
        val middleBar = view.findViewById<CardView>(R.id.middleBar)
        val bottomCard = view.findViewById<CardView>(R.id.news)

        animateCard(topBar, 0)
        animateCard(middleBar, 100)
        animateCard(bottomCard, 200)

        viewPager = view.findViewById(R.id.newsViewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabIndicator)

        // Load news from Firebase
        val database = FirebaseDatabase.getInstance()
        val newsRef = database.getReference("news")

        newsRef.orderByChild("timestamp").get()
            .addOnSuccessListener { snapshot ->
                Log.d("FirebaseDebug", "News data snapshot received: ${snapshot.exists()}")

                val newsList = mutableListOf<NewsItem>()
                for (child in snapshot.children) {
                    val item = child.getValue(NewsItem::class.java)
                    if (item != null) {
                        newsList.add(item)
                    } else {
                        Log.e("FirebaseDebug", "Null news item at key: ${child.key}")
                    }
                }

                Log.d("FirebaseDebug", "Loaded ${newsList.size} news items")

                if (newsList.isEmpty()) {
                    Toast.makeText(requireContext(), "No news available", Toast.LENGTH_SHORT).show()
                }

                viewPager.adapter = NewsAdapter(newsList)
                TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

                // Start auto scroll after 3 seconds delay
                autoScrollHandler.postDelayed(autoScrollRunnable, 3000)
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseDebug", "Failed to load news", exception)
                Toast.makeText(requireContext(), "Failed to load news: ${exception.message}", Toast.LENGTH_LONG).show()
            }

        // Pause auto-scroll on touch and resume after release
        viewPager.getChildAt(0).setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> autoScrollHandler.removeCallbacks(autoScrollRunnable)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> autoScrollHandler.postDelayed(autoScrollRunnable, 3000)
            }
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        autoScrollHandler.removeCallbacks(autoScrollRunnable)
    }

    private fun animateCard(card: View, delay: Long) {
        card.alpha = 0f
        card.translationY = 100f

        card.animate()
            .alpha(1f)
            .translationY(0f)
            .setStartDelay(delay)
            .setDuration(500)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }

    // Data class for Firebase news items (with explicit no-arg constructor)
    data class NewsItem(
        val imageUrl: String = "",
        val title: String = "",
        val date: String = "",
        val content: String = "",
        val timestamp: Long = 0
    ) {
        constructor() : this("", "", "", "", 0)
    }

    // Adapter for ViewPager2 news
    inner class NewsAdapter(private val newsList: List<NewsItem>) :
        RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.newsImage)
            val title: TextView = view.findViewById(R.id.newsTitle)
            val date: TextView = view.findViewById(R.id.newsDate)
            val readMore: TextView = view.findViewById(R.id.readMore)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            return NewsViewHolder(view)
        }

        override fun getItemCount() = newsList.size

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val news = newsList[position]

            Glide.with(holder.image.context)
                .load(news.imageUrl)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)

            holder.title.text = news.title

            // Format timestamp to readable date string
            val dateString = if (news.timestamp != 0L) {
                SimpleDateFormat("EEE, dd MMM · hh:mm a", Locale.getDefault())
                    .format(Date(news.timestamp))
            } else {
                "Unknown Date"
            }
            holder.date.text = dateString

            holder.readMore.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Read more: ${news.title}", Toast.LENGTH_SHORT).show()
                // TODO: Open full content in a dialog or new screen if needed
            }
        }
    }
}
