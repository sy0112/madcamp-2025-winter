package com.example.androidlab.ui.register

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R

/**
 * [ImageSelectAdapter] 클래스 설명:
 * 사용자가 선택한 로컬 사진(Uri)이나 기존에 업로드된 서버 사진(URL -> Uri)을 목록에 보여줍니다.
 * 삭제 버튼(X)을 통해 선택된 사진을 리스트에서 제거할 수 있습니다.
 */
class ImageSelectAdapter(
    private val imageUris: MutableList<Uri>,
    private val onRemoveClick: (Int) -> Unit // 삭제 버튼 클릭 시 호출될 콜백
) : RecyclerView.Adapter<ImageSelectAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSelectedImage: ImageView = view.findViewById(R.id.ivSelectedImage)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemoveImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_image, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUri = imageUris[position]
        
        Glide.with(holder.ivSelectedImage.context)
            .load(currentUri)
            .centerCrop()
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(holder.ivSelectedImage)

        // 삭제 버튼 클릭 이벤트
        holder.btnRemove.setOnClickListener {
            onRemoveClick(holder.adapterPosition)
        }
    }

    override fun getItemCount() = imageUris.size
}
