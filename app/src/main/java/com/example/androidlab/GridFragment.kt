// package com.example.androidlab.ui

// import android.os.Bundle
// import android.util.Log
// import android.view.View
// import androidx.fragment.app.Fragment
// import androidx.recyclerview.widget.GridLayoutManager
// import androidx.recyclerview.widget.RecyclerView
// import com.example.androidlab.Project
// import com.example.androidlab.R
// import com.google.firebase.firestore.Query
// import com.google.firebase.firestore.ktx.firestore
// import com.google.firebase.ktx.Firebase

// class GridFragment : Fragment(R.layout.fragment_grid) {

//     private val db = Firebase.firestore
//     private lateinit var adapter: ProjectAdapter
//     private val projectList = mutableListOf<Project>()

//     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//         super.onViewCreated(view, savedInstanceState)

//         // 1. XML에 있는 RecyclerView 연결
//         val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

//         // 2. 어댑터 초기화 및 연결
//         adapter = ProjectAdapter(projectList)
//         recyclerView.adapter = adapter

//         // 3. 2열 그리드 레이아웃 설정
//         recyclerView.layoutManager = GridLayoutManager(context, 2)

//         // 4. 데이터 불러오기 실행
//         fetchProjects()
//     }

//     private fun fetchProjects() {
//         // "projects" 컬렉션에서 생성시간(createdAt) 내림차순으로 정렬해서 가져오기
//         db.collection("projects")
//             .orderBy("createdAt", Query.Direction.DESCENDING)
//             .addSnapshotListener { snapshot, e ->
//                 if (e != null) {
//                     Log.e("Firestore", "데이터 로드 실패: ${e.message}")
//                     return@addSnapshotListener
//                 }

//                 val newList = mutableListOf<Project>()
//                 snapshot?.let {
//                     for (doc in it) {
//                         // Firestore 문서를 Project 클래스 객체로 자동 변환
//                         val project = doc.toObject(Project::class.java)
//                         newList.add(project)
//                     }
//                     // 어댑터의 데이터를 갱신 (화면에 새로고침 됨)
//                     adapter.updateData(newList)
//                 }
//             }
//     }
// }
