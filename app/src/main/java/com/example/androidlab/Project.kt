package com.example.androidlab

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : BaseActivity() {   // 🔥 AppCompatActivity → BaseActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 🔽 하단 네비게이션 설정 (Grid 선택 상태)
        setupBottomNavigation(R.id.nav_grid)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val projects = listOf(
            Project(
                title = "Android 앱 개발",
                description = "Android 앱을 설계하고 구현한 프로젝트입니다.",
                members = "강승수, 박새연",
                images = listOf(
                    R.drawable.project1_1,
                    R.drawable.project1_2,
                    R.drawable.project1_3
                )
            ),
            Project(
                title = "웹사이트 디자인",
                description = "반응형 웹사이트 디자인 및 구현 프로젝트",
                members = "서민훈, 정다훈",
                images = listOf(
                    R.drawable.project2_1,
                    R.drawable.project2_2,
                    R.drawable.project2_3
                )
            ),
            Project(
                title = "머신러닝",
                description = "머신러닝 모델 학습 프로젝트",
                members = "이영희, 김철수",
                images = listOf(
                    R.drawable.project3_1,
                    R.drawable.project3_2,
                    R.drawable.project3_3
                )
            )
        )

        recyclerView.adapter = MainGridAdapter(projects) { project ->
            val intent = Intent(this, ProjectDetailActivity::class.java)
            intent.putExtra("title", project.title)
            intent.putExtra("description", project.description)
            intent.putExtra("members", project.members)
            intent.putIntegerArrayListExtra(
                "images",
                ArrayList(project.images)
            )
            startActivity(intent)
        }
    }
}
