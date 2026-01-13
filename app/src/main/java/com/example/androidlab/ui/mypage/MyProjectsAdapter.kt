package com.example.androidlab.ui.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.models.Project
import com.example.androidlab.R

class MyProjectsAdapter(
    private var projects: List<Project>,
    private val onEditClick: (Project) -> Unit,
    private val onDeleteClick: (Project) -> Unit
) : RecyclerView.Adapter<MyProjectsAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val btnEdit: Button = view.findViewById(R.id.btnEdit)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun getItemCount() = projects.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_my_project, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val project = projects[position]

        holder.tvTitle.text = project.title
        holder.btnEdit.setOnClickListener { onEditClick(project) }
        holder.btnDelete.setOnClickListener { onDeleteClick(project)

        }
        }

    fun updateProjects(newProjects: List<Project>) {
        this.projects = newProjects
        notifyDataSetChanged()
    }



}