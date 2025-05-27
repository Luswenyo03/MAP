package com.example.myhockey

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

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
        savedInstanceState: Bundle?
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

        val newsList = listOf(
            NewsItem(
                R.drawable.news_1,
                "Hockey finals coming soon!",
                "Sat, 25 May · 3:30PM",
                "The finals are scheduled for next weekend with all teams ready."
            ),
            NewsItem(
                R.drawable.news_2,
                "Training camp recap",
                "Fri, 24 May · 10:00AM",
                "Highlights and key takeaways from this week's training camp."
            ),
            NewsItem(
                R.drawable.news_3,
                "Top 5 goals this season",
                "Thu, 23 May · 6:00PM",
                "A countdown of the best goals scored this season."
            ),
            NewsItem(
                R.drawable.news_4,
                "Interview with MVP",
                "Wed, 22 May · 2:00PM",
                "An exclusive interview with the league's most valuable player."
            ),
            NewsItem(
                R.drawable.news_5,
                "League standings update",
                "Tue, 21 May · 1:00PM",
                "Latest rankings and statistics of the current hockey season."
            )
        )

        viewPager.adapter = NewsAdapter(newsList)

        // Attach TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        // Start auto scroll after 3 seconds delay
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000)

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

    // News data class with content field added
    data class NewsItem(
        val imageRes: Int,
        val title: String,
        val date: String,
        val content: String
    )

    // RecyclerView Adapter for news
    inner class NewsAdapter(private val newsList: List<NewsItem>) :
        RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        inner class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.newsImage)
            val title: TextView = view.findViewById(R.id.newsTitle)
            val date: TextView = view.findViewById(R.id.newsDate)
            val readMore: TextView = view.findViewById(R.id.readMore)
            // content field is NOT displayed yet
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            return NewsViewHolder(view)
        }

        override fun getItemCount() = newsList.size

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            val news = newsList[position]
            holder.image.setImageResource(news.imageRes)
            holder.title.text = news.title
            holder.date.text = news.date

            holder.readMore.setOnClickListener {
                Toast.makeText(holder.itemView.context, "Read more: ${news.title}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
