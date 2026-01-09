package com.example.androidlab

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 메인 화면용 데이터
data class MainItem(
    val imageResId: Int,
    val title: String
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        // 한 줄에 2개짜리 이미지 그리드
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val items = listOf(
            MainItem(R.drawable.project1, "Android 앱 개발"),
            MainItem(R.drawable.project2, "웹사이트 디자인"),
            MainItem(R.drawable.project3, "머신러닝")
        )

        recyclerView.adapter = MainGridAdapter(items) { clickedItem ->
            // ✅ 클릭한 이미지(프로젝트) 정보 전달
            val intent = Intent(this, ProjectListActivity::class.java)
            intent.putExtra("project_title", clickedItem.title)
            startActivity(intent)
        }
    }
}
