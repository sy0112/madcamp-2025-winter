package com.example.androidlab.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.R
import com.example.androidlab.models.Project // 수정됨

class ListFragment : Fragment(R.layout.fragment_list) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val projects = sampleProjects()

        recyclerView.adapter = ListAdapter(projects) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, it)
                .addToBackStack(null)
                .commit()
        }
    }

    private fun sampleProjects(): List<Project> {
        return listOf(
            Project(
                title = "Android 앱 개발",
                description = "Android 앱 프로젝트 설명",
                members = "강승수, 박새연",
                images = listOf(R.drawable.project1_1, R.drawable.project1_2, R.drawable.project1_3)
            ),
            Project(
                title = "웹사이트 디자인",
                description = "웹 디자인 프로젝트",
                members = "서민훈, 정다훈",
                images = listOf(R.drawable.project2_1, R.drawable.project2_2, R.drawable.project2_3)
            ),
            Project(
                title = "iOS 앱 개발",
                description = "iOS 앱 프로젝트 설명",
                members = "김철수, 이영희",
                images = listOf(R.drawable.project3_1, R.drawable.project3_2, R.drawable.project3_3)
            )
        )
    }
}
