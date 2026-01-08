package com.example.androidlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    // main activity는 앱이 실행될 때 가장 먼저 실행되는 화면임.

    override fun onCreate(savedInstanceState: Bundle?) {
        // 액티비티가 생성될 때 자동으로 호출
        super.onCreate(savedInstanceState)

        // ✅ XML 연결
        setContentView(R.layout.activity_main)
        // 이 activity의 화면으로 사용하겠다. 이 xml에 있는 것들이 실제 화면에 나타남.

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // 변수에 참조 연결. 이건 activity_main.xml에 있음.

        recyclerView.layoutManager = LinearLayoutManager(this)
        // 어떤 방향 규칙으로 배치할 지 정하는 관리자.

        // 더미 데이터
        val items = listOf(
            "아이템 1",
            "아이템 2",
            "아이템 3",
            "아이템 4"
        )

        recyclerView.adapter = ItemAdapter(items)
    }
}
