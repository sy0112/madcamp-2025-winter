package com.example.androidlab.ui.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R

class ImagePagerAdapter(
    private val imageUrls: List<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_pager, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val url = imageUrls[position]
        Glide.with(holder.imageView.context)
            .load(url)
            .placeholder(ColorDrawable(Color.WHITE))
            .error(ColorDrawable(Color.WHITE))
            .centerCrop()
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = if (imageUrls.isEmpty()) 1 else imageUrls.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int, payloads: MutableList<Any>) {
        if (imageUrls.isEmpty()) {
            holder.imageView.setImageDrawable(ColorDrawable(Color.WHITE))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }
}
