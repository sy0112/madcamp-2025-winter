package com.example.androidlab.ui.register

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.androidlab.R
import com.example.androidlab.ui.grid.GridFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private var selectedImageUri: Uri? = null
    private lateinit var ivProjectImage: ImageView

    // 사진 선택을 위한 Launcher
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            ivProjectImage.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivProjectImage = view.findViewById(R.id.ivProjectImage)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etMembers = view.findViewById<EditText>(R.id.etMembers)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        btnSelectImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnRegister.setOnClickListener {
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

            if (selectedImageUri != null) {
                uploadImageAndSaveData(currentUser.uid, currentUser.email, title, description, members, selectedImageUri!!)
            } else {
                saveProjectData(currentUser.uid, currentUser.email, title, description, members, null)
            }
        }
    }

    private fun uploadImageAndSaveData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String,
        imageUri: Uri
    ) {
        val fileName = "project_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    saveProjectData(uid, email, title, description, members, uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("Storage", "업로드 실패", e)
                Toast.makeText(requireContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveProjectData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String,
        imageUrl: String?
    ) {
        val projectData = hashMapOf(
            "ownerUid" to uid,
            "ownerEmail" to email,
            "title" to title,
            "description" to description,
            "members" to members,
            "imageUrl" to imageUrl,
            "createdAt" to System.currentTimeMillis()
        )

        db.collection("projects")
            .add(projectData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "프로젝트가 등록되었습니다.", Toast.LENGTH_SHORT).show()
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
