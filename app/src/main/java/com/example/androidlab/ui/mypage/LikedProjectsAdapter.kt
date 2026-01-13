package com.example.androidlab.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Project

class LikedProjectsAdapter(private var projects: List<Project>,
    private val onItemClick: (Project) -> Unit) :
    RecyclerView.Adapter<LikedProjectsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivThumb: ImageView = view.findViewById(R.id.ProjectThumb)
        val tvTitle: TextView = view.findViewById(R.id.tvProjectTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_liked_project, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.tvTitle.text = project.title

        if (project.imageUrls.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(project.imageUrls[0])
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivThumb)
        } else {
            holder.ivThumb.setImageResource(android.R.drawable.ic_menu_gallery)
        }
        holder.itemView.setOnClickListener { onItemClick(project) }
    }

    override fun getItemCount() = projects.size

    fun updateProjects(newProjects: List<Project>) {
        this.projects = newProjects
        notifyDataSetChanged()
    }

}