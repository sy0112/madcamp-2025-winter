package com.example.androidlab.ui.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidlab.LoginActivity
import com.example.androidlab.R
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MyPageFragment : Fragment(R.layout.fragment_mypage) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnMyProjects = view.findViewById<Button>(R.id.btnMyProjects)
        val btnLikedProjects = view.findViewById<Button>(R.id.btnLikedProjects)
        val btnLogout = view.findViewById<Button>(R.id.btnLogout)

        // 1. 내 프로젝트 보기: MyProjectsFragment로 이동
        btnMyProjects.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, MyProjectsFragment())
                .addToBackStack(null)
                .commit()
        }

        // 2. 하트 누른 프로젝트 보기 (기능 구현 전 토스트)
        btnLikedProjects.setOnClickListener {
            Toast.makeText(requireContext(), "좋아요한 프로젝트 목록 기능 준비 중입니다.", Toast.LENGTH_SHORT).show()
        }

        // 3. 로그아웃: Firebase 로그아웃 후 로그인 화면으로 이동
        btnLogout.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
            
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
