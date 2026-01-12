package com.example.androidlab.ui.register

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R

class ImageSelectAdapter(private val imageUris: List<Uri>) :
    RecyclerView.Adapter<ImageSelectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSelectedImage: ImageView = view.findViewById(R.id.ivSelectedImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.ivSelectedImage.setImageURI(imageUris[position])
    }

    override fun getItemCount() = imageUris.size
}
