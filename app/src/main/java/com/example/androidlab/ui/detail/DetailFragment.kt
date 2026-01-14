package com.example.androidlab.ui.detail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Comment
import com.example.androidlab.models.Project
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private lateinit var project: Project
    
    private lateinit var ivLike: ImageView
    private lateinit var tvLikeCount: TextView
    private lateinit var layoutLike: View
    private lateinit var commentAdapter: CommentAdapter

    companion object {
        fun newInstance(project: Project): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString("projectId", project.id)
            args.putString("title", project.title)
            args.putString("description", project.description)
            args.putString("githubUrl", project.githubUrl)
            args.putString("members", project.members)
            args.putString("ownerUid", project.ownerUid)
            args.putString("ownerEmail", project.ownerEmail)
            args.putStringArrayList("imageUrls", ArrayList(project.imageUrls))
            args.putStringArrayList("likedBy", ArrayList(project.likedBy))
            args.putLong("createdAt", project.createdAt)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectId = arguments?.getString("projectId") ?: return
        val title = arguments?.getString("title") ?: ""
        val description = arguments?.getString("description") ?: ""
        val githubUrl = arguments?.getString("githubUrl") ?: ""
        val members = arguments?.getString("members") ?: ""
        val ownerUid = arguments?.getString("ownerUid") ?: ""
        val ownerEmail = arguments?.getString("ownerEmail") ?: ""
        val imageUrls = arguments?.getStringArrayList("imageUrls") ?: arrayListOf<String>()
        val likedBy = arguments?.getStringArrayList("likedBy") ?: arrayListOf<String>()
        val createdAt = arguments?.getLong("createdAt") ?: 0L

        project = Project(
            id = projectId,
            title = title,
            description = description,
            members = members,
            githubUrl = githubUrl,
            ownerUid = ownerUid,
            ownerEmail = ownerEmail,
            imageUrls = imageUrls,
            createdAt = createdAt,
            likedBy = likedBy
        )

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        val layoutGithub = view.findViewById<View>(R.id.layoutGithub)
        ivLike = view.findViewById(R.id.ivLike)
        tvLikeCount = view.findViewById(R.id.tvLikeCount)
        layoutLike = view.findViewById(R.id.layoutLike)

        tvTitle.text = project.title
        tvDescription.text = project.description
        tvLikeCount.text = project.likedBy.size.toString()

        viewPager.adapter = DetailImageAdapter(project.imageUrls)

        // 1. 초기 UI 설정
        updateLikeUI()

        // 2. 프로젝트 실시간 데이터 감시 (하트 실시간 동기화)
        db.collection("projects").document(project.id)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                
                val updatedProject = snapshot.toObject(Project::class.java)?.copy(id = snapshot.id)
                if (updatedProject != null) {
                    project = updatedProject
                    updateLikeUI()
                }
            }

        layoutGithub.setOnClickListener {
            if (project.githubUrl.isNotEmpty()) {
                var url = project.githubUrl
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "https://" + url
                }
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "유효하지 않은 주소입니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "등록된 GitHub 링크가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        layoutLike.setOnClickListener {
            toggleLike()
        }

        setupComments(view)
    }

    private fun toggleLike() {
        val user = auth.currentUser ?: return
        val docRef = db.collection("projects").document(project.id)

        val isLiked = project.likedBy.contains(user.uid)

        if (isLiked) {
            // 좋아요 취소
            docRef.update("likedBy", FieldValue.arrayRemove(user.uid))
        } else {
            // 좋아요 실행
            playFunnyHeartAnim(layoutLike)
            playCenterHeartAnim()
            docRef.update("likedBy", FieldValue.arrayUnion(user.uid))
        }
        // [참고] snapshotListener가 있으므로 로컬 수동 업데이트 코드는 제거하여 서버 데이터와 일치시킵니다.
    }

    private fun updateLikeUI() {
        val user = auth.currentUser
        val isLiked = user != null && project.likedBy.contains(user.uid)
        
        if (isLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_filled)
            ivLike.setColorFilter(android.graphics.Color.RED) // 채워진 하트는 빨간색으로
        } else {
            ivLike.setImageResource(R.drawable.ic_heart_outline)
            ivLike.setColorFilter(android.graphics.Color.BLACK) // 빈 하트는 검은색(또는 기본색)으로
        }
        tvLikeCount.text = project.likedBy.size.toString()
    }

    private fun playFunnyHeartAnim(view: View) {
        val shake = ObjectAnimator.ofFloat(view, "translationX", 0f, 15f, -15f, 15f, -15f, 0f)
        shake.duration = 250
        val rotate = ObjectAnimator.ofFloat(view, "rotationY", 0f, 360f)
        rotate.duration = 500
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 1.4f, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 1.4f, 1f)
        val scale = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY)
        scale.duration = 400
        AnimatorSet().apply {
            playSequentially(shake, rotate)
            playTogether(rotate, scale)
            start()
        }
    }

    private fun playCenterHeartAnim() {
        val ivBigHeart = view?.findViewById<ImageView>(R.id.ivBigHeart) ?: return
        ivBigHeart.visibility = View.VISIBLE
        ivBigHeart.alpha = 1f
        ivBigHeart.scaleX = 0f
        ivBigHeart.scaleY = 0f
        val scaleX = ObjectAnimator.ofFloat(ivBigHeart, View.SCALE_X, 0f, 4.5f)
        val scaleY = ObjectAnimator.ofFloat(ivBigHeart, View.SCALE_Y, 0f, 4.5f)
        val alpha = ObjectAnimator.ofFloat(ivBigHeart, View.ALPHA, 1f, 0f)
        AnimatorSet().apply {
            playTogether(scaleX, scaleY, alpha)
            duration = 1200
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    ivBigHeart.visibility = View.GONE
                }
            })
            start()
        }
    }

    private fun setupComments(view: View) {
        val rvComments = view.findViewById<RecyclerView>(R.id.rvComments)
        val etComment = view.findViewById<EditText>(R.id.etComment)
        val btnSendComment = view.findViewById<ImageButton>(R.id.btnSendComment)

        commentAdapter = CommentAdapter(emptyList())
        rvComments.layoutManager = LinearLayoutManager(requireContext())
        rvComments.adapter = commentAdapter

        db.collection("projects").document(project.id)
            .collection("comments")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null) {
                    val comments = snapshot.toObjects(Comment::class.java)
                    commentAdapter.updateComments(comments)
                }
            }

        btnSendComment.setOnClickListener {
            val content = etComment.text.toString().trim()
            val user = auth.currentUser
            if (user == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (content.isEmpty()) return@setOnClickListener

            val commentData = hashMapOf(
                "userId" to user.uid,
                "userName" to (user.displayName ?: "익명"),
                "userProfileUrl" to (user.photoUrl?.toString() ?: ""),
                "content" to content,
                "createdAt" to System.currentTimeMillis()
            )

            db.collection("projects").document(project.id)
                .collection("comments")
                .add(commentData)
                .addOnSuccessListener { etComment.text.clear() }
        }
    }
}

class DetailImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image_pager, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(imageUrls[position]).into(holder.imageView)
    }
    override fun getItemCount(): Int = imageUrls.size
}
