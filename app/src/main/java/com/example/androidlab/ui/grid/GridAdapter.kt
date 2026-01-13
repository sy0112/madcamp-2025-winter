package com.example.androidlab.ui.grid

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.detail.DetailFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * [GridAdapter] 클래스 설명:
 * 메인 화면의 그리드(격자) 목록에 각 프로젝트 데이터를 뿌려주는 어댑터입니다.
 */
class GridAdapter(
    private var items: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<GridAdapter.VH>() {

    private val currentUserUid = Firebase.auth.currentUser?.uid
    private val db = Firebase.firestore

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProject)
        val title: TextView = view.findViewById(R.id.tvTitle)
        val team: TextView = view.findViewById(R.id.tvTeam)
        val heartCount: TextView = view.findViewById(R.id.tvHeartCount)
        val ivHeart: ImageView = view.findViewById(R.id.ivHeart)
        val layoutHeart: View = view.findViewById(R.id.layoutHeart) // 하트 클릭 영역 추가
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_grid, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val project = items[position]
        
        // 1. 이미지 표시
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
        
        // 2. 텍스트 정보 표시
        holder.title.text = project.title
        holder.team.text = project.members
        holder.heartCount.text = project.likedBy.size.toString()

        // 3. 하트 상태 표시
        val isLikedByMe = currentUserUid != null && project.likedBy.contains(currentUserUid)
        if (isLikedByMe) {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_filled)
            holder.ivHeart.setColorFilter(Color.RED)
        } else {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_outline)
            holder.ivHeart.setColorFilter(Color.WHITE)
        }

        // 4. 하트 클릭 이벤트 (목록에서 바로 좋아요 토글)
        holder.layoutHeart.setOnClickListener {
            if (currentUserUid == null) {
                Toast.makeText(holder.itemView.context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docRef = db.collection("projects").document(project.id)
            if (isLikedByMe) {
                docRef.update("likedBy", FieldValue.arrayRemove(currentUserUid))
            } else {
                docRef.update("likedBy", FieldValue.arrayUnion(currentUserUid))
            }
        }

        // 5. 항목 전체 클릭 이벤트 (상세화면 이동)
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
