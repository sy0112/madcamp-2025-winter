package com.example.androidlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // 더미 데이터: Project 객체 리스트
        val projects = listOf(
            Project(
                name = "프로젝트 A",
                description = "이 프로젝트는 A를 구현하는 프로젝트입니다.",
                members = "홍길동, 김철수"
            ),
            Project(
                name = "프로젝트 B",
                description = "이 프로젝트는 B를 구현하는 프로젝트입니다.",
                members = "이영희, 박민수"
            ),
            Project(
                name = "프로젝트 C",
                description = "이 프로젝트는 C를 구현하는 프로젝트입니다.",
                members = "최수민, 강승수"
            )
        )

        // Adapter 연결
        recyclerView.adapter = ProjectAdapter(projects)
    }
}
