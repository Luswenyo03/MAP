package com.example.myhockey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class news_updates : Fragment() {

    // News data class - reuse from your other fragment or move to a shared file
    data class NewsItem(val imageRes: Int, val title: String, val date: String, val content: String)

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_news_updates, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.news_update)

        val newsList = listOf(
            NewsItem(R.drawable.news_1, "Hockey finals coming soon!", "Sat, 25 May · 3:30PM", "The hockey finals are scheduled to take place next weekend. Teams have been preparing rigorously and fans are excited to witness some thrilling matches."),
            NewsItem(R.drawable.news_2, "Training camp recap", "Fri, 24 May · 10:00AM", "The recent training camp was a huge success with intensive drills and great participation from all players."),
            NewsItem(R.drawable.news_3, "Top 5 goals this season", "Thu, 23 May · 6:00PM", "Here is a countdown of the top 5 goals scored in the current hockey season, showcasing some spectacular skills."),
            NewsItem(R.drawable.news_4, "Interview with MVP", "Wed, 22 May · 2:00PM", "We sat down with this season’s MVP to talk about their journey, training, and aspirations for the future."),
            NewsItem(R.drawable.news_5, "League standings update", "Tue, 21 May · 1:00PM", "Check out the latest updates on the league standings as teams battle for top positions.")
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = ExpandableNewsAdapter(newsList)
    }

    inner class ExpandableNewsAdapter(private val newsList: List<NewsItem>) :
        RecyclerView.Adapter<ExpandableNewsAdapter.ExpandableNewsViewHolder>() {

        inner class ExpandableNewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val image: ImageView = view.findViewById(R.id.newsImage)
            val title: TextView = view.findViewById(R.id.newsTitle)
            val date: TextView = view.findViewById(R.id.newsDate)
            val content: TextView = view.findViewById(R.id.newsContent)
            //val cardView: CardView = view.findViewById(R.id.cardContainer)
        }

        private val expandedPositions = mutableSetOf<Int>()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpandableNewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news_expandable, parent, false)
            return ExpandableNewsViewHolder(view)
        }

        override fun getItemCount() = newsList.size

        override fun onBindViewHolder(holder: ExpandableNewsViewHolder, position: Int) {
            val news = newsList[position]
            holder.image.setImageResource(news.imageRes)
            holder.title.text = news.title
            holder.date.text = news.date
            holder.content.text = news.content

            val isExpanded = expandedPositions.contains(position)
            holder.content.visibility = if (isExpanded) View.VISIBLE else View.GONE

            holder.itemView.setOnClickListener {
                if (isExpanded) {
                    expandedPositions.remove(position)
                } else {
                    expandedPositions.add(position)
                }
                notifyItemChanged(position)
            }
        }
    }
}
