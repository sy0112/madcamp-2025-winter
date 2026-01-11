package com.example.androidlab.ui.grid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project // 수정됨
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
        // images 필드가 비어있지 않은지 확인하는 안전한 코드 추가
        if (project.images.isNotEmpty()) {
            holder.img.setImageResource(project.images.first())
        }
        holder.title.text = project.title

        holder.itemView.setOnClickListener {
            onClick(DetailFragment.newInstance(project))
        }
    }

    override fun getItemCount() = items.size
}
