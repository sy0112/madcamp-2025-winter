package com.example.androidlab.ui.grid

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.detail.DetailFragment

class GridAdapter(
    private var items: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<GridAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProject)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val team: TextView = view.findViewById(R.id.tvTeam)
        val heartCount: TextView = view.findViewById(R.id.tvHeartCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val project = items[position]
        
        if (project.imageUrls.isNotEmpty()) {
            Glide.with(holder.img.context)
                .load(project.imageUrls.first())
                .placeholder(ColorDrawable(Color.WHITE))
                .error(ColorDrawable(Color.WHITE))
                .centerCrop()
                .into(holder.img)
        } else {
            holder.img.setImageDrawable(ColorDrawable(Color.WHITE))
        }
        
        holder.title.text = project.title
        holder.team.text = project.members // 팀원 정보를 표시
        holder.heartCount.text = project.likedBy.size.toString()

        holder.itemView.setOnClickListener {
            onClick(DetailFragment.newInstance(project))
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<Project>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}
