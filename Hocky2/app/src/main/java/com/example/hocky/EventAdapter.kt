package com.example.hocky

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(
    private val eventList: MutableList<Event>,
    private val onItemClick: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTv: TextView = itemView.findViewById(R.id.name)
        val dateTv: TextView = itemView.findViewById(R.id.date)
        val locationTv: TextView = itemView.findViewById(R.id.location)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(eventList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.nameTv.text = event.name
        holder.dateTv.text = event.date
        holder.locationTv.text = event.location
    }

    override fun getItemCount(): Int = eventList.size

    fun updateEvents(newEvents: List<Event>) {
        eventList.clear()
        eventList.addAll(newEvents)
        notifyDataSetChanged()
    }
}

