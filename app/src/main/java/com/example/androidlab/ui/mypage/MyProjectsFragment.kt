package com.example.androidlab.ui.mypage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidlab.models.Project
import com.example.androidlab.R
import com.example.androidlab.ui.register.RegisterFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MyProjectsFragment : Fragment(R.layout.fragment_my_projects) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private lateinit var adapter: MyProjectsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvMyProjects)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = MyProjectsAdapter(
            projects = emptyList(),
            onEditClick = { project ->
                val fragment = RegisterFragment()
                val bundle = Bundle().apply {
                    putString("projectId", project.id)
                    putString("title", project.title)
                    putString("description", project.description)
                    putString("members", project.members)
                }
                fragment.arguments = bundle

                parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            },
            onDeleteClick = { project ->
                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("프로젝트 삭제")
                    .setMessage("${project.title}을(를) 삭제하시겠습니까?")
                    .setPositiveButton("삭제") { _, _ ->
                        db.collection("projects").document(project.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "${project.title} 삭제 성공",
                                    Toast.LENGTH_SHORT
                                ).show()
                                fetchMyProjects()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "삭제 실패: ${e.message}",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                    }
                    .setNegativeButton("취소", null).show()
            }
        )
        recyclerView.adapter = adapter

        fetchMyProjects()
    }

    private fun fetchMyProjects() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("projects")
            .whereEqualTo("ownerUid", uid)
            .get()
            .addOnSuccessListener { result ->
                val myProjectList = mutableListOf<Project>()

                for (document in result) {
                    val project = document.toObject(Project::class.java).copy(id = document.id)
                    myProjectList.add(project)
                }
                adapter.updateProjects(myProjectList)
            }
            .addOnFailureListener { e ->
                android.util.Log.e("MyProjects", "데이터 가져오기 실패", e)
            }
    }
}