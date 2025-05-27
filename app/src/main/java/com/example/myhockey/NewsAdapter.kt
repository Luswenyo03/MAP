package com.example.myhockey

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private val newsList: List<NewsItem>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    data class NewsItem(val imageRes: Int, val title: String, val date: String)

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
        holder.image.setImageResource(news.imageRes)
        holder.title.text = news.title
        holder.date.text = news.date
        holder.readMore.setOnClickListener {
            Toast.makeText(holder.itemView.context, "Opening news: ${news.title}", Toast.LENGTH_SHORT).show()
        }
    }
}
