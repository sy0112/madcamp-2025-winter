package com.example.androidlab.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * [CommentAdapter] 클래스 설명:
 * 프로젝트 상세 화면 하단에 표시될 덧글 목록을 관리하는 어댑터입니다.
 */
class CommentAdapter(
    private var comments: List<Comment>
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProfile: ImageView = view.findViewById(R.id.ivCommentProfile)
        val tvUserName: TextView = view.findViewById(R.id.tvCommentUserName)
        val tvDate: TextView = view.findViewById(R.id.tvCommentDate)
        val tvContent: TextView = view.findViewById(R.id.tvCommentContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = comments[position]
        
        holder.tvUserName.text = comment.userName
        holder.tvContent.text = comment.content
        
        // 날짜 포맷팅 (예: 2025.01.01 12:30)
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(comment.createdAt))

        // 프로필 이미지 로드
        Glide.with(holder.ivProfile.context)
            .load(comment.userProfileUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_gallery)
            .circleCrop()
            .into(holder.ivProfile)
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<Comment>) {
        this.comments = newComments
        notifyDataSetChanged()
    }
}
