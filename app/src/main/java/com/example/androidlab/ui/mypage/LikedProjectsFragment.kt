package com.example.androidlab.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.models.Project
import com.example.androidlab.R
import com.example.androidlab.ui.detail.DetailFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class LikedProjectsFragment : Fragment(R.layout.fragment_liked_projects) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private lateinit var adapter : LikedProjectsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvLikedProjects)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = LikedProjectsAdapter(emptyList()) { project ->
            val detailFragment = DetailFragment.newInstance(project)

            parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null).commit()
        }
        recyclerView.adapter = adapter

        fetchLikedProjects()
    }
    private fun fetchLikedProjects() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("projects")
            .whereArrayContains("likedBy", uid)
            .get()
            .addOnSuccessListener { result ->
                val likedList = mutableListOf<Project>()
                for (document in result) {
                    val project = document.toObject(Project::class.java).copy(id = document.id)
                    likedList.add(project)
                }
                adapter.updateProjects(likedList)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("LikedProjects", "데이터 가져오기 실패", e)
            }

    }
}