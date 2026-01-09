package com.example.androidlab.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.androidlab.R

class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etMembers = view.findViewById<EditText>(R.id.etMembers)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val title = etTitle.text.toString()
            val description = etDescription.text.toString()
            val members = etMembers.text.toString()

            // 여기서 DB나 리스트에 저장 로직 추가 가능
            Toast.makeText(requireContext(), "프로젝트 등록 완료: $title", Toast.LENGTH_SHORT).show()

            // 등록 후 GridFragment로 돌아가기
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, com.example.androidlab.ui.grid.GridFragment())
                .commit()
        }
    }
}
