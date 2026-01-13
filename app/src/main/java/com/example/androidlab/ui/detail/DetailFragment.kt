package com.example.androidlab.ui.detail

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.androidlab.R
import com.example.androidlab.models.Comment
import com.example.androidlab.models.Project
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
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
    
    private lateinit var commentAdapter: CommentAdapter

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
        val dateView = view.findViewById<TextView>(R.id.tvDetailDate)
        val ivLike = view.findViewById<ImageView>(R.id.ivLike)
        val tvLikeCount = view.findViewById<TextView>(R.id.tvLikeCount)
        
        // 덧글 관련 뷰
        val rvComments = view.findViewById<RecyclerView>(R.id.rvComments)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val btnSendComment = view.findViewById<ImageButton>(R.id.btnSendComment)

        title.text = arguments?.getString("title")
        desc.text = arguments?.getString("description")

        // 날짜 포맷팅
        val createdAt = arguments?.getLong("createdAt") ?: 0L
        val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        dateView.text = sdf.format(Date(createdAt))

        val imageUrls = arguments?.getStringArrayList("imageUrls") ?: arrayListOf()
        pager.adapter = ImagePagerAdapter(imageUrls)

        val currentUser = auth.currentUser
        
        // 덧글 리사이클러뷰 설정
        commentAdapter = CommentAdapter(emptyList())
        rvComments.layoutManager = LinearLayoutManager(requireContext())
        rvComments.adapter = commentAdapter

        // 1. 덧글 실시간 불러오기
        projectId?.let { id ->
            db.collection("projects").document(id).collection("comments")
                .orderBy("createdAt", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) return@addSnapshotListener
                    val commentList = snapshot?.map { doc ->
                        doc.toObject(Comment::class.java).copy(id = doc.id)
                    } ?: emptyList()
                    commentAdapter.updateComments(commentList)
                }
        }

        // 2. 덧글 전송 버튼 클릭
        btnSendComment.setOnClickListener {
            val content = etComment.text.toString().trim()
            if (content.isEmpty()) return@setOnClickListener
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val commentData = hashMapOf(
                "userId" to currentUser.uid,
                "userName" to (currentUser.displayName ?: "익명"),
                "userProfileUrl" to currentUser.photoUrl.toString(),
                "content" to content,
                "createdAt" to System.currentTimeMillis()
            )

            projectId?.let { id ->
                db.collection("projects").document(id).collection("comments")
                    .add(commentData)
                    .addOnSuccessListener {
                        etComment.text.clear() // 전송 후 입력창 비우기
                    }
            }
        }

        // 좋아요 상태 설정 및 이벤트
        val initialLikedBy = arguments?.getStringArrayList("likedBy") ?: arrayListOf()
        updateLikeUI(currentUser?.uid, initialLikedBy, ivLike, tvLikeCount)

        view.findViewById<View>(R.id.layoutLike).setOnClickListener {
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (projectId == null) return@setOnClickListener
            val docRef = db.collection("projects").document(projectId!!)
            if (isLiked) docRef.update("likedBy", FieldValue.arrayRemove(currentUser.uid))
            else docRef.update("likedBy", FieldValue.arrayUnion(currentUser.uid))
        }

        projectId?.let { id ->
            db.collection("projects").document(id)
                .addSnapshotListener { snapshot, _ ->
                    val project = snapshot?.toObject(Project::class.java)
                    project?.let { updateLikeUI(currentUser?.uid, it.likedBy, ivLike, tvLikeCount) }
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
