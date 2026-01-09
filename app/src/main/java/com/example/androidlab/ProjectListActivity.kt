package com.example.androidlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Item(val projectName: String, val projectDescription: String, val groupMember: String)
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

        val items = listOf(
            Item("Android 앱 개발", "Android 앱 개발 프로젝트", "강승수 박새연"),
            Item("웹사이트 디자인", "웹 사이트 디자인 및 구현", "서민훈 정다훈"),
            Item("머신러닝 모델 학습", "머신러닝 모델 학습", "이영희 김철수"),
            Item("IoT 센서 분석", "IoT 센서 데이터 분석", "최수민 박민수"),
            Item("게임 개발", "게임 개발 프로젝트", "홍길동 이수진"),
            Item("데이터 시각화 웹 앱", "데이터 시각화 웹 앱 프로젝트", "박철호 김영희"),
            Item("자율주행 시뮬레이션", "자율주행 차량 시뮬레이션", "강승수 정다훈"),
            Item("블록체인 투표 시스템", "블록체인 기반 투표 시스템", "서민훈 박민수"),
            Item("챗봇 개발", "챗봇 개발 및 테스트", "이영희 최수민"),
            Item("클라우드 서버 배포", "클라우드 서버 배포 프로젝트", "홍길동 박새연")
        )


        recyclerView.adapter = ItemAdapter(items)
    }
}

