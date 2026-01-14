package com.example.androidlab.ui.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.detail.DetailFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * [ListAdapter] 클래스 설명:
 * 세로 리스트 형태로 프로젝트 정보를 보여주는 어댑터입니다.
 */
class ListAdapter(
    private var projects: List<Project>,
    private val onClick: (DetailFragment) -> Unit
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private val db = Firebase.firestore

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvProjectTitle)
        val description: TextView = view.findViewById(R.id.tvProjectDescription)
        val members: TextView = view.findViewById(R.id.tvProjectMembers)
        val heartCount: TextView = view.findViewById(R.id.tvListHeartCount)
        val date: TextView = view.findViewById(R.id.tvProjectDate)
        val ivHeart: ImageView = view.findViewById(R.id.ivListHeart)
        val layoutHeart: View = view.findViewById(R.id.layoutHeart)
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

        // 1. 날짜 표시
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        holder.date.text = sdf.format(Date(project.createdAt))

        // [핵심 수정] 매번 현재 로그인 유저를 확인하여 하트 상태 결정
        val currentUser = Firebase.auth.currentUser
        val isLikedByMe = currentUser != null && project.likedBy.contains(currentUser.uid)

        // 2. 하트 상태 표시
        if (isLikedByMe) {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_filled)
            holder.ivHeart.setColorFilter(Color.RED)
        } else {
            holder.ivHeart.setImageResource(R.drawable.ic_heart_outline)
            holder.ivHeart.setColorFilter(Color.GRAY)
        }

        // 3. 하트 클릭 이벤트 (목록에서 바로 토글)
        holder.layoutHeart.setOnClickListener {
            val user = Firebase.auth.currentUser
            if (user == null) {
                Toast.makeText(holder.itemView.context, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val docRef = db.collection("projects").document(project.id)
            if (project.likedBy.contains(user.uid)) {
                docRef.update("likedBy", FieldValue.arrayRemove(user.uid))
            } else {
                docRef.update("likedBy", FieldValue.arrayUnion(user.uid))
            }
        }

        // 4. 항목 전체 클릭 (상세화면 이동)
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
