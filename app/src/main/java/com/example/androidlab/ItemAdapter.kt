package com.example.androidlab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(private val items: List<Item>) :
    RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProjectName: TextView = itemView.findViewById(R.id.tv_project_name)
        val tvProjectDescription: TextView = itemView.findViewById(R.id.tv_project_description)
        val tvGroupMember: TextView = itemView.findViewById(R.id.tv_group_member)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvProjectName.text = item.projectName
        holder.tvProjectDescription.text = item.projectDescription
        holder.tvGroupMember.text = item.groupMember
    }

    override fun getItemCount(): Int = items.size
}
