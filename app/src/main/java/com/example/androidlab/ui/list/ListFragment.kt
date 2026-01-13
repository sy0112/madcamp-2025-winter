package com.example.androidlab.ui.list

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * [ListFragment] 클래스 설명:
 * 프로젝트들을 세로 리스트 형태로 보여주는 화면입니다.
 * GridFragment와 동일하게 실시간 정렬 스타일 업데이트 기능을 포함합니다.
 */
class ListFragment : Fragment(R.layout.fragment_list) {

    private val db = Firebase.firestore
    private lateinit var listAdapter: ListAdapter
    private var currentSortMode = "latest" // "latest" 또는 "likes"
    private var allProjects = listOf<Project>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        listAdapter = ListAdapter(emptyList()) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = listAdapter

        // 정렬 버튼 설정
        val btnSortLatest = view.findViewById<Button>(R.id.btnSortLatest)
        val btnSortLikes = view.findViewById<Button>(R.id.btnSortLikes)

        btnSortLatest.setOnClickListener {
            currentSortMode = "latest"
            sortAndDisplay()
        }
        btnSortLikes.setOnClickListener {
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
        val btnSortLatest = view?.findViewById<Button>(R.id.btnSortLatest)
        val btnSortLikes = view?.findViewById<Button>(R.id.btnSortLikes)

        val sortedList = when (currentSortMode) {
            "latest" -> {
                btnSortLatest?.updateStyle(true)
                btnSortLikes?.updateStyle(false)
                allProjects.sortedByDescending { it.createdAt }
            }
            "likes" -> {
                btnSortLatest?.updateStyle(false)
                btnSortLikes?.updateStyle(true)
                allProjects.sortedByDescending { it.likedBy.size }
            }
            else -> allProjects
        }
        listAdapter.updateItems(sortedList)
    }

    /**
     * 버튼의 선택 상태에 따라 시각적 스타일을 업데이트합니다.
     */
    private fun Button.updateStyle(isSelected: Boolean) {
        if (isSelected) {
            this.setTextColor(Color.BLACK)
            this.setTypeface(null, Typeface.BOLD)
            this.paintFlags = this.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
        } else {
            this.setTextColor(Color.parseColor("#BBBBBB"))
            this.setTypeface(null, Typeface.NORMAL)
            this.paintFlags = this.paintFlags and android.graphics.Paint.UNDERLINE_TEXT_FLAG.inv()
        }
    }
}
