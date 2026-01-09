package com.example.androidlab

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProjectAdapter(private val projects: List<Project>) :
    RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProjectName: TextView = itemView.findViewById(R.id.tvProjectName)
        val tvProjectDescription: TextView = itemView.findViewById(R.id.tvProjectDescription)
        val tvProjectMembers: TextView = itemView.findViewById(R.id.tvProjectMembers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false) // 바꾼 XML 이름
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.tvProjectName.text = project.name
        holder.tvProjectDescription.text = project.description
        holder.tvProjectMembers.text = project.members
    }

    override fun getItemCount(): Int = projects.size
}
