package com.example.androidlab.ui.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidlab.R
import com.example.androidlab.ui.grid.GridFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth // 현재 로그인된 사용자 정보 가져오기

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etMembers = view.findViewById<EditText>(R.id.etMembers)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            // 1. 현재 로그인된 유저 확인
            val currentUser = auth.currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val members = etMembers.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 2. 저장할 데이터 맵 생성 (ownerUid 추가가 핵심!)
            val projectData = hashMapOf(
                "ownerUid" to currentUser.uid,       // 등록한 사람의 고유 ID
                "ownerEmail" to currentUser.email,   // (선택사항) 등록한 사람의 이메일
                "title" to title,
                "description" to description,
                "members" to members,
                "createdAt" to System.currentTimeMillis()
            )

            // 3. Firestore에 저장
            db.collection("projects")
                .add(projectData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "프로젝트가 등록되었습니다.", Toast.LENGTH_SHORT).show()

                    // 등록 성공 후 화면 이동
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, GridFragment())
                        .commit()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "저장 에러", e)
                    Toast.makeText(requireContext(), "등록 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }
}