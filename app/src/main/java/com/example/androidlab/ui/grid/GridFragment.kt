package com.example.androidlab.ui.grid

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project
import com.example.androidlab.ui.register.RegisterFragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * [GridFragment] 클래스 설명:
 * 등록된 프로젝트들을 그리드 형태로 보여주며, 정렬 상태에 따라 버튼의 스타일이 변경됩니다.
 */
class GridFragment : Fragment(R.layout.fragment_grid) {

    private val db = Firebase.firestore
    private lateinit var gridAdapter: GridAdapter
    private var currentSortMode = "latest" // "latest" 또는 "likes"
    private var allProjects = listOf<Project>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. RecyclerView 설정
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        // 2. 어댑터 초기화
        gridAdapter = GridAdapter(emptyList()) { detailFragment ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = gridAdapter

        // 3. 정렬 버튼 이벤트 설정
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

        // 4. 데이터 불러오기 시작
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

    /**
     * 정렬 로직 및 버튼 UI 업데이트
     */
    private fun sortAndDisplay() {
        val btnSortLatest = view?.findViewById<Button>(R.id.btnSortLatest)
        val btnSortLikes = view?.findViewById<Button>(R.id.btnSortLikes)

        val sortedList = when (currentSortMode) {
            "latest" -> {
                // 최신순 활성화 스타일
                btnSortLatest?.updateStyle(true)
                btnSortLikes?.updateStyle(false)
                allProjects.sortedByDescending { it.createdAt }
            }
            "likes" -> {
                // 좋아요순 활성화 스타일
                btnSortLatest?.updateStyle(false)
                btnSortLikes?.updateStyle(true)
                allProjects.sortedByDescending { it.likedBy.size }
            }
            else -> allProjects
        }
        gridAdapter.updateItems(sortedList)
    }

    // 버튼의 스타일(색상, 굵기)을 변경하는 확장 함수
    private fun Button.updateStyle(isSelected: Boolean) {
        if (isSelected) {
            this.setTextColor(Color.BLACK) // 선택 시 검정색
            this.setTypeface(null, Typeface.BOLD) // 선택 시 굵게
            this.alpha = 1.0f
        } else {
            this.setTextColor(Color.LTGRAY) // 미선택 시 밝은 회색
            this.setTypeface(null, Typeface.NORMAL) // 미선택 시 보통 굵기
            this.alpha = 0.8f // 약간 투명하게
        }
    }
}
