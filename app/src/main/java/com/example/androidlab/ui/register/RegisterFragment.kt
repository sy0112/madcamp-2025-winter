package com.example.androidlab.ui.register

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.androidlab.R
import com.example.androidlab.ui.grid.GridFragment
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.SetOptions
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var currentProjectId: String? = null

    private val selectedImageUris = mutableListOf<Uri>()
    private lateinit var rvImages: RecyclerView
    private lateinit var imageAdapter: ImageSelectAdapter

    private val cloudinaryConfig = mapOf(
        "cloud_name" to "dlotgejuu",
        "api_key" to "398175322742183",
        "api_secret" to "j0dWzx_Ke1NSOEmA1pVrWvYY5tE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            MediaManager.init(requireContext(), cloudinaryConfig)
        } catch (e: IllegalStateException) {
        }
    }

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvImages = view.findViewById(R.id.rvImages)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etMembers = view.findViewById<EditText>(R.id.etMembers)
        val etGithubUrl = view.findViewById<EditText>(R.id.etGithubUrl) // 🌟 추가
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)

        currentProjectId = arguments?.getString("projectId")

        if (currentProjectId != null) {
            etTitle.setText(arguments?.getString("title"))
            etDescription.setText(arguments?.getString("description"))
            etMembers.setText(arguments?.getString("members"))
            etGithubUrl.setText(arguments?.getString("githubUrl")) // 🌟 추가
            btnRegister.text = "수정 완료"
        }

        imageAdapter = ImageSelectAdapter(selectedImageUris) { position ->
            selectedImageUris.removeAt(position)
            imageAdapter.notifyDataSetChanged()
        }
        
        rvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = imageAdapter

        btnSelectImage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
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
            val githubUrl = etGithubUrl.text.toString().trim() // 🌟 추가

            if (title.isEmpty() || description.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedImageUris.isNotEmpty()) {
                uploadImagesAndSaveData(currentUser.uid, currentUser.email, title, description, members, githubUrl)
            } else {
                saveProjectData(currentUser.uid, currentUser.email, title, description, members, githubUrl, emptyList())
            }
        }
    }

    private fun uploadImagesAndSaveData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String,
        githubUrl: String // 🌟 추가
    ) {
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0

        selectedImageUris.forEach { uri ->
            MediaManager.get().upload(uri)
                .unsigned("madcamp_winter_2025")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        val imageUrl = resultData?.get("secure_url") as? String
                        if (imageUrl != null) uploadedUrls.add(imageUrl)
                        uploadCount++
                        if (uploadCount == selectedImageUris.size) {
                            saveProjectData(uid, email, title, description, members, githubUrl, uploadedUrls)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        uploadCount++
                        if (uploadCount == selectedImageUris.size) {
                            saveProjectData(uid, email, title, description, members, githubUrl, uploadedUrls)
                        }
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    private fun saveProjectData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String,
        githubUrl: String, // 🌟 추가
        imageUrls: List<String>
    ) {
        val projectData = mutableMapOf<String, Any?>(
            "ownerUid" to uid,
            "ownerEmail" to email,
            "title" to title,
            "description" to description,
            "members" to members,
            "githubUrl" to githubUrl, // 🌟 추가
            "createdAt" to System.currentTimeMillis()
        )

        if (imageUrls.isNotEmpty()) {
            projectData["imageUrls"] = imageUrls
        }

        if (currentProjectId != null) {
            db.collection("projects").document(currentProjectId!!)
                .set(projectData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "수정 실패", e)
                    Toast.makeText(requireContext(), "수정 실패", Toast.LENGTH_SHORT).show()
                }
        } else {
            projectData["imageUrls"] = imageUrls
            db.collection("projects").add(projectData)
                .addOnSuccessListener {
                    explode()
                    Toast.makeText(requireContext(), "프로젝트가 등록되었습니다.", Toast.LENGTH_SHORT).show()
                    
                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, GridFragment())
                            .commit()
                    }, 1500)
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "등록 실패", e)
                    Toast.makeText(requireContext(), "등록 실패", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun explode() {
        val konfettiView = view?.findViewById<KonfettiView>(R.id.konfettiView) ?: return
        
        val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            position = Position.Relative(0.5, 0.3),
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )
        konfettiView.start(party)
    }
}
