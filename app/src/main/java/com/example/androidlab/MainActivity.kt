package com.example.androidlab

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.androidlab.ui.detail.DetailFragment
import com.example.androidlab.ui.grid.GridFragment
import com.example.androidlab.ui.list.ListFragment
import com.example.androidlab.ui.register.RegisterFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. 로그인 상태 확인
        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            // 로그인이 안 되어 있다면 로그인 화면으로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) // 새로운 화면 띄우는 명령어
            finish()
            return
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // 기본 화면: GridFragment
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, GridFragment())
        }

        // 하단 네비게이션 클릭
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_grid -> {
                    openFragment(GridFragment())
                    true
                }
                R.id.menu_list -> {
                    openFragment(ListFragment())
                    true
                }
                R.id.menu_register -> {
                    openFragment(RegisterFragment()) // 등록 화면
                    true
                }
                else -> false
            }
        }
    }

    private fun openFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null) // 뒤로가기 시 이전 화면으로
        }
    }
}
