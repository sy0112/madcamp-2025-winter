package com.example.androidlab.ui.detail

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class DetailFragment : Fragment(R.layout.fragment_detail) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private lateinit var project: Project
    
    private lateinit var ivLike: ImageView
    private lateinit var tvLikeCount: TextView
    private lateinit var layoutLike: View

    companion object {
        fun newInstance(project: Project): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putString("projectId", project.id)
            args.putString("title", project.title)
            args.putString("description", project.description)
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
        val imageUrls = arguments?.getStringArrayList("imageUrls") ?: listOf<String>()
        val likedBy = arguments?.getStringArrayList("likedBy") ?: listOf<String>()
        val createdAt = arguments?.getLong("createdAt") ?: 0L

        project = Project(projectId, title, description, "", "", "", imageUrls, createdAt, likedBy)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPager)
        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescription)
        ivLike = view.findViewById(R.id.ivLike)
        tvLikeCount = view.findViewById(R.id.tvLikeCount)
        layoutLike = view.findViewById(R.id.layoutLike)

        tvTitle.text = project.title
        tvDescription.text = project.description
        tvLikeCount.text = project.likedBy.size.toString()

        viewPager.adapter = DetailImageAdapter(project.imageUrls)

        updateLikeUI()

        layoutLike.setOnClickListener {
            toggleLike()
        }

        setupComments(view)
    }

    private fun toggleLike() {
        val user = auth.currentUser ?: return
        val docRef = db.collection("projects").document(project.id)

        val isLiked = project.likedBy.contains(user.uid)
        val newLikedBy = project.likedBy.toMutableList()

        if (isLiked) {
            newLikedBy.remove(user.uid)
            docRef.update("likedBy", FieldValue.arrayRemove(user.uid))
        } else {
            playFunnyHeartAnim(layoutLike) 
            playCenterHeartAnim()         // 🌟 업데이트된 중앙 애니메이션 호출
            newLikedBy.add(user.uid)
            docRef.update("likedBy", FieldValue.arrayUnion(user.uid))
        }

        project = project.copy(likedBy = newLikedBy)
        updateLikeUI()
    }

    private fun updateLikeUI() {
        val user = auth.currentUser
        val isLiked = user != null && project.likedBy.contains(user.uid)
        
        if (isLiked) {
            ivLike.setImageResource(R.drawable.ic_heart_filled)
        } else {
            ivLike.setImageResource(R.drawable.ic_heart_outline)
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

    // 🌟 1200ms 동안 천천히 커지며 부드럽게 사라지는 애니메이션
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
            duration = 1200 // 더 천천히 (기존 800ms -> 1200ms)
            interpolator = AccelerateDecelerateInterpolator() // 시작과 끝이 더 부드럽게
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
        rvComments.layoutManager = LinearLayoutManager(requireContext())
    }
}

class DetailImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_pager, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageUrls[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}
