package com.example.androidlab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProject_name: TextView = itemView.findViewById(R.id.tv_project_name)
        val tvProject_description: TextView = itemView.findViewById(R.id.tv_project_description)
        val tvGroup_member: TextView = itemView.findViewById(R.id.tv_group_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvProject_name.text = item.project_name
        holder.tvProject_description.text = item.project_description
        holder.tvGroup_member.text = item.group_member
    }

    override fun getItemCount(): Int = items.size
}
