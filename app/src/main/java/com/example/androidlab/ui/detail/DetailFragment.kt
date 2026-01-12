package com.example.androidlab.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var projectId: String? = null
    private var isLiked = false

    companion object {
        fun newInstance(project: Project): DetailFragment {
            val f = DetailFragment()
            f.arguments = Bundle().apply {
                putString("id", project.id)
                putString("title", project.title)
                putString("description", project.description)
                putString("members", project.members)
                putLong("createdAt", project.createdAt)
                putStringArrayList("imageUrls", ArrayList(project.imageUrls))
                putStringArrayList("likedBy", ArrayList(project.likedBy))
            }
            return f
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        projectId = arguments?.getString("id")
        val pager = view.findViewById<ViewPager2>(R.id.viewPager)
        val title = view.findViewById<TextView>(R.id.tvTitle)
        val desc = view.findViewById<TextView>(R.id.tvDescription)
        val members = view.findViewById<TextView>(R.id.tvMembers)
        val dateView = view.findViewById<TextView>(R.id.tvDetailDate)
        val ivLike = view.findViewById<ImageView>(R.id.ivLike)
        val tvLikeCount = view.findViewById<TextView>(R.id.tvLikeCount)

        title.text = arguments?.getString("title")
        desc.text = arguments?.getString("description")
        members.text = "팀원: ${arguments?.getString("members")}"

        // 날짜 포맷팅
        val createdAt = arguments?.getLong("createdAt") ?: 0L
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        dateView.text = sdf.format(Date(createdAt))

        val imageUrls = arguments?.getStringArrayList("imageUrls") ?: arrayListOf()
        pager.adapter = ImagePagerAdapter(imageUrls)

        val currentUser = auth.currentUser
        val initialLikedBy = arguments?.getStringArrayList("likedBy") ?: arrayListOf()
        
        // 초기 좋아요 상태 설정
        updateLikeUI(currentUser?.uid, initialLikedBy, ivLike, tvLikeCount)

        // 하트 클릭 이벤트
        view.findViewById<View>(R.id.layoutLike).setOnClickListener {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (projectId == null) return@setOnClickListener

            val docRef = db.collection("projects").document(projectId!!)
            
            if (isLiked) {
                // 좋아요 취소
                docRef.update("likedBy", FieldValue.arrayRemove(currentUser.uid))
            } else {
                // 좋아요 추가
                docRef.update("likedBy", FieldValue.arrayUnion(currentUser.uid))
            }
        }

        // 실시간 좋아요 상태 감시
        projectId?.let { id ->
            db.collection("projects").document(id)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w("DetailFragment", "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val project = snapshot.toObject(Project::class.java)
                        project?.let {
                            updateLikeUI(currentUser?.uid, it.likedBy, ivLike, tvLikeCount)
                        }
                    }
                }
        }
    }

    private fun updateLikeUI(currentUid: String?, likedBy: List<String>, ivLike: ImageView, tvLikeCount: TextView) {
        isLiked = currentUid != null && likedBy.contains(currentUid)
        tvLikeCount.text = likedBy.size.toString()

        if (isLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_filled)
            ivLike.setColorFilter(android.graphics.Color.RED)
        } else {
            ivLike.setImageResource(R.drawable.ic_heart_outline)
            ivLike.setColorFilter(android.graphics.Color.BLACK)
        }
    }
}
