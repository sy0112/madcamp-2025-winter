package com.example.androidlab.ui.grid

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.Project

class GridFragment : Fragment(R.layout.fragment_grid) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)

        val projects = sampleProjects()

        recyclerView.adapter = GridAdapter(projects) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun sampleProjects(): List<Project> {
        return listOf(
            Project(
                "Android 앱 개발",
                "Android 앱 프로젝트 설명",
                "강승수, 박새연",
                listOf(R.drawable.project1_1, R.drawable.project1_2, R.drawable.project1_3)
            ),
            Project(
                "웹사이트 디자인",
                "웹 디자인 프로젝트",
                "서민훈, 정다훈",
                listOf(R.drawable.project2_1, R.drawable.project2_2, R.drawable.project2_3)
            ),
            Project(
                "iOS 앱 개발",
                "iOS 앱 프로젝트 설명",
                "김철수, 이영희",
                listOf(R.drawable.project3_1, R.drawable.project3_2, R.drawable.project3_3)
        )
        )
    }
}
