package com.example.androidlab.ui.grid

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class GridFragment : Fragment(R.layout.fragment_grid) {

    private val db = Firebase.firestore
    private lateinit var gridAdapter: GridAdapter
    private var currentSortMode = "latest" // "latest" or "likes"
    private var allProjects = listOf<Project>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        gridAdapter = GridAdapter(emptyList()) { detailFragment ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = gridAdapter

        // 정렬 버튼 설정
        view.findViewById<Button>(R.id.btnSortLatest).setOnClickListener {
            currentSortMode = "latest"
            sortAndDisplay()
        }
        view.findViewById<Button>(R.id.btnSortLikes).setOnClickListener {
            currentSortMode = "likes"
            sortAndDisplay()
        }

        fetchProjectsFromFirestore()
    }

    private fun fetchProjectsFromFirestore() {
        db.collection("projects")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    allProjects = snapshot.map { doc ->
                        doc.toObject(Project::class.java).copy(id = doc.id)
                    }
                    sortAndDisplay()
                }
            }
    }

    private fun sortAndDisplay() {
        val sortedList = when (currentSortMode) {
            "latest" -> allProjects.sortedByDescending { it.createdAt }
            "likes" -> allProjects.sortedByDescending { it.likedBy.size }
            else -> allProjects
        }
        gridAdapter.updateItems(sortedList)
    }
}
