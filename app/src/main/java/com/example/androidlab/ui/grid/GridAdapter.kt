package com.example.androidlab.ui.grid
import com.example.androidlab.Project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.ui.detail.DetailFragment

class GridAdapter(
    private val items: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<GridAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProject)
        val title: TextView = view.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val project = items[position]
        holder.img.setImageResource(project.images.first())
        holder.title.text = project.title

        holder.itemView.setOnClickListener {
            onClick(DetailFragment.newInstance(project))
        }
    }

    override fun getItemCount() = items.size
}
