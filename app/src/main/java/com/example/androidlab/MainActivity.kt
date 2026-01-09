package com.example.androidlab

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.androidlab.ui.detail.DetailFragment
import com.example.androidlab.ui.grid.GridFragment
import com.example.androidlab.ui.list.ListFragment
import com.example.androidlab.ui.register.RegisterFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
