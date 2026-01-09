package com.example.androidlab

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2


class ProjectDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        val viewPager = findViewById<ViewPager2>(R.id.viewPagerImages)
        val tvTitle = findViewById<TextView>(R.id.tvDetailTitle)
        val tvDescription = findViewById<TextView>(R.id.tvDetailDescription)
        val tvMembers = findViewById<TextView>(R.id.tvDetailMembers)

        // 🔹 Intent 데이터 받기
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val members = intent.getStringExtra("members")
        val images = intent.getIntegerArrayListExtra("images") ?: arrayListOf()

        // 🔹 텍스트 표시
        tvTitle.text = title
        tvDescription.text = description
        tvMembers.text = "팀원: $members"

        // 🔹 ViewPager에 이미지 연결
        viewPager.adapter = ImagePagerAdapter(images)
    }
}
