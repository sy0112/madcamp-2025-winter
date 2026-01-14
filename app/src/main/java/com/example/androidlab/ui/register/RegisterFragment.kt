package com.example.androidlab.ui.register

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import java.io.File
import java.io.FileOutputStream
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
        } catch (e: IllegalStateException) {}
    }

    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.addAll(uris)
            if (selectedImageUris.size > 5) {
                Toast.makeText(requireContext(), "최대 5장까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                val subList = ArrayList(selectedImageUris.take(5))
                selectedImageUris.clear()
                selectedImageUris.addAll(subList)
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvHeader = view.findViewById<TextView>(R.id.tvHeader)
        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val etDescription = view.findViewById<EditText>(R.id.etDescription)
        val etMembers = view.findViewById<EditText>(R.id.etMembers)
        val etGithubUrl = view.findViewById<EditText>(R.id.etGithubUrl)
        rvImages = view.findViewById(R.id.rvImages)
        val btnRegister = view.findViewById<Button>(R.id.btnRegister)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)

        currentProjectId = arguments?.getString("projectId")

        if (currentProjectId != null) {
            tvHeader?.text = "프로젝트 수정"
            btnRegister.text = "수정 완료"
            etTitle.setText(arguments?.getString("title"))
            etDescription.setText(arguments?.getString("description"))
            etMembers.setText(arguments?.getString("members"))
            etGithubUrl.setText(arguments?.getString("githubUrl"))
            
            val existingUrls = arguments?.getStringArrayList("imageUrls") ?: arrayListOf()
            if (selectedImageUris.isEmpty()) {
                existingUrls.forEach { url -> selectedImageUris.add(Uri.parse(url)) }
            }
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
            val githubUrl = etGithubUrl.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "처리 중..."

            processImagesAndSave(currentUser.uid, currentUser.email, title, description, members, githubUrl)
        }
    }

    /**
     * 이미지 업로드 순서를 선택한 순서대로 보장하여 저장합니다.
     */
    private fun processImagesAndSave(uid: String, email: String?, title: String, description: String, members: String, githubUrl: String) {
        // [순서 보장] 원래 리스트 크기만큼 null로 채워진 리스트 생성
        val finalUrls = arrayOfNulls<String>(selectedImageUris.size)
        var processCount = 0

        if (selectedImageUris.isEmpty()) {
            saveProjectData(uid, email, title, description, members, githubUrl, emptyList())
            return
        }

        selectedImageUris.forEachIndexed { index, uri ->
            if (uri.toString().startsWith("http")) {
                // 이미 서버에 있는 사진은 즉시 해당 위치에 삽입
                finalUrls[index] = uri.toString()
                processCount++
                if (processCount == selectedImageUris.size) {
                    saveProjectData(uid, email, title, description, members, githubUrl, finalUrls.filterNotNull())
                }
            } else {
                // 새 로컬 사진은 압축 후 업로드
                val compressedFile = compressImage(uri)
                if (compressedFile != null) {
                    MediaManager.get().upload(compressedFile.absolutePath)
                        .unsigned("madcamp_winter_2025")
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String?) {}
                            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                                val imageUrl = resultData?.get("secure_url") as? String
                                // [순서 보장] 원래 위치(index)에 결과 URL 삽입
                                if (imageUrl != null) finalUrls[index] = imageUrl
                                processCount++
                                compressedFile.delete()
                                if (processCount == selectedImageUris.size) {
                                    saveProjectData(uid, email, title, description, members, githubUrl, finalUrls.filterNotNull())
                                }
                            }
                            override fun onError(requestId: String?, error: ErrorInfo?) {
                                processCount++
                                compressedFile.delete()
                                if (processCount == selectedImageUris.size) {
                                    saveProjectData(uid, email, title, description, members, githubUrl, finalUrls.filterNotNull())
                                }
                            }
                            override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                        }).dispatch()
                } else {
                    processCount++
                    if (processCount == selectedImageUris.size) {
                        saveProjectData(uid, email, title, description, members, githubUrl, finalUrls.filterNotNull())
                    }
                }
            }
        }
    }

    private fun compressImage(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            val scale = Math.min(1280f / bitmap.width, 1280f / bitmap.height)
            val finalBitmap = if (scale < 1) Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true) else bitmap
            val tempFile = File.createTempFile("compressed_", ".jpg", requireContext().cacheDir)
            val out = FileOutputStream(tempFile)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            tempFile
        } catch (e: Exception) { null }
    }

    private fun saveProjectData(uid: String, email: String?, title: String, description: String, members: String, githubUrl: String, imageUrls: List<String>) {
        val projectData = mutableMapOf<String, Any?>(
            "ownerUid" to uid,
            "ownerEmail" to email,
            "title" to title,
            "description" to description,
            "members" to members,
            "githubUrl" to githubUrl,
            "imageUrls" to imageUrls,
            "createdAt" to System.currentTimeMillis()
        )

        if (currentProjectId != null) {
            db.collection("projects").document(currentProjectId!!)
                .set(projectData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "수정되었습니다.", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
        } else {
            db.collection("projects").add(projectData)
                .addOnSuccessListener {
                    explode()
                    Toast.makeText(requireContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, GridFragment()).commit()
                    }, 1500)
                }
        }
    }

    private fun explode() {
        val konfettiView = view?.findViewById<KonfettiView>(R.id.konfettiView) ?: return
        konfettiView.start(Party(speed = 0f, maxSpeed = 30f, damping = 0.9f, spread = 360, colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def), position = Position.Relative(0.5, 0.3), emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)))
    }
}
