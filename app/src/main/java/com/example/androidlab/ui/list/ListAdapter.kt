package com.example.androidlab.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.detail.DetailFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListAdapter(
    private var projects: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvProjectTitle)
        val description: TextView = view.findViewById(R.id.tvProjectDescription)
        val members: TextView = view.findViewById(R.id.tvProjectMembers)
        val heartCount: TextView = view.findViewById(R.id.tvListHeartCount)
        val date: TextView = view.findViewById(R.id.tvProjectDate)
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
        holder.heartCount.text = project.likedBy.size.toString()

        // 날짜 포맷팅
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        holder.date.text = sdf.format(Date(project.createdAt))

        holder.itemView.setOnClickListener {
            onClick(DetailFragment.newInstance(project))
        }
    }

    override fun getItemCount(): Int = projects.size

    fun updateItems(newItems: List<Project>) {
        this.projects = newItems
        notifyDataSetChanged()
    }
}
