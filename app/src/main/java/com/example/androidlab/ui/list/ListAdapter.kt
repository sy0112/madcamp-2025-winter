package com.example.androidlab.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project // 수정됨
import com.example.androidlab.ui.detail.DetailFragment

class ListAdapter(
    private val projects: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvProjectTitle)
        val description: TextView = view.findViewById(R.id.tvProjectDescription)
        val members: TextView = view.findViewById(R.id.tvProjectMembers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val project = projects[position]
        holder.title.text = project.title
        holder.description.text = project.description
        holder.members.text = "팀원: ${project.members}"

        holder.itemView.setOnClickListener {
            onClick(DetailFragment.newInstance(project))
        }
    }

    override fun getItemCount(): Int = projects.size
}
