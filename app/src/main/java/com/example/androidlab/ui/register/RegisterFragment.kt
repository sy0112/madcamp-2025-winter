package com.example.androidlab.ui.register

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * [RegisterFragment] 설명:
 * 프로젝트 신규 등록 및 기존 프로젝트 수정을 담당하는 화면입니다.
 * 사진 선택 및 삭제(X 버튼) 기능이 포함되어 있습니다.
 */
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    // 최종적으로 저장될 이미지들의 Uri 목록 (로컬 Uri 또는 서버 URL 기반 Uri)
    private val selectedImageUris = mutableListOf<Uri>()
    private var projectId: String? = null

    private lateinit var rvImages: RecyclerView
    private lateinit var imageAdapter: ImageSelectAdapter
    private lateinit var btnRegister: Button

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

    // 사진첩에서 사진 추가
    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            // 기존 목록에 추가 (최대 5장 제한 유지하려면 로직 추가 가능)
            selectedImageUris.addAll(uris)
            if (selectedImageUris.size > 5) {
                Toast.makeText(requireContext(), "최대 5장까지만 가능합니다.", Toast.LENGTH_SHORT).show()
                val subList = selectedImageUris.take(5)
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
        rvImages = view.findViewById(R.id.rvImages)
        btnRegister = view.findViewById(R.id.btnRegister)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage)

        // 초기 데이터 세팅 (수정 모드일 때)
        projectId = arguments?.getString("projectId")
        if (projectId != null) {
            tvHeader?.text = "프로젝트 수정"
            btnRegister.text = "수정 완료"
            etTitle.setText(arguments?.getString("title"))
            etDescription.setText(arguments?.getString("description"))
            etMembers.setText(arguments?.getString("members"))
            
            // 기존 이미지 URL들을 Uri로 변환하여 리스트에 미리 추가
            val existingUrls = arguments?.getStringArrayList("imageUrls") ?: arrayListOf()
            if (selectedImageUris.isEmpty()) { // 화면 회전 시 중복 추가 방지
                existingUrls.forEach { url ->
                    selectedImageUris.add(Uri.parse(url))
                }
            }
        }

        // 어댑터 설정 (삭제 콜백 포함)
        imageAdapter = ImageSelectAdapter(selectedImageUris) { position ->
            // X 버튼 클릭 시 리스트에서 제거
            selectedImageUris.removeAt(position)
            imageAdapter.notifyItemRemoved(position)
            imageAdapter.notifyItemRangeChanged(position, selectedImageUris.size)
        }
        
        rvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvImages.adapter = imageAdapter

        btnSelectImage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnRegister.setOnClickListener {
            val currentUser = auth.currentUser ?: return@setOnClickListener
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val members = etMembers.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnRegister.isEnabled = false
            btnRegister.text = "저장 중..."

            // 이미지 업로드 및 저장 시작
            processImagesAndSave(currentUser.uid, currentUser.email, title, description, members)
        }
    }

    /**
     * 이미지 목록을 분석하여 새 이미지는 업로드하고, 기존 이미지는 유지하여 저장합니다.
     */
    private fun processImagesAndSave(uid: String, email: String?, title: String, description: String, members: String) {
        val finalUrls = mutableListOf<String>()
        val urisToUpload = mutableListOf<Uri>()
        
        // 1. 기존 서버 URL(http 시작)과 새로 선택된 로컬 Uri 구분
        selectedImageUris.forEach { uri ->
            if (uri.toString().startsWith("http")) {
                finalUrls.add(uri.toString()) // 이미 서버에 있는 건 그대로 유지
            } else {
                urisToUpload.add(uri) // 로컬 파일은 업로드 대상
            }
        }

        if (urisToUpload.isEmpty()) {
            // 업로드할 새 사진이 없으면 바로 저장
            saveProjectData(uid, email, title, description, members, finalUrls)
        } else {
            // 새 사진들 업로드 시작
            var uploadCount = 0
            urisToUpload.forEach { uri ->
                val compressedFile = compressImage(uri)
                if (compressedFile != null) {
                    MediaManager.get().upload(compressedFile.absolutePath)
                        .unsigned("madcamp_winter_2025")
                        .callback(object : UploadCallback {
                            override fun onStart(requestId: String?) {}
                            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}
                            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                                val imageUrl = resultData?.get("secure_url") as? String
                                if (imageUrl != null) finalUrls.add(imageUrl)
                                uploadCount++
                                compressedFile.delete()
                                if (uploadCount == urisToUpload.size) {
                                    saveProjectData(uid, email, title, description, members, finalUrls)
                                }
                            }
                            override fun onError(requestId: String?, error: ErrorInfo?) {
                                uploadCount++
                                compressedFile.delete()
                                if (uploadCount == urisToUpload.size) {
                                    saveProjectData(uid, email, title, description, members, finalUrls)
                                }
                            }
                            override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                        }).dispatch()
                } else {
                    uploadCount++
                    if (uploadCount == urisToUpload.size) saveProjectData(uid, email, title, description, members, finalUrls)
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
            val finalBitmap = if (scale < 1) {
                Bitmap.createScaledBitmap(bitmap, (bitmap.width * scale).toInt(), (bitmap.height * scale).toInt(), true)
            } else {
                bitmap
            }
            val tempFile = File.createTempFile("compressed_", ".jpg", requireContext().cacheDir)
            val out = FileOutputStream(tempFile)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            out.flush()
            out.close()
            tempFile
        } catch (e: Exception) {
            null
        }
    }

    private fun saveProjectData(uid: String, email: String?, title: String, description: String, members: String, imageUrls: List<String>) {
        val projectData = hashMapOf(
            "ownerUid" to uid,
            "ownerEmail" to email,
            "title" to title,
            "description" to description,
            "members" to members,
            "imageUrls" to imageUrls,
            "createdAt" to System.currentTimeMillis()
        )

        val task = if (projectId == null) {
            db.collection("projects").add(projectData)
        } else {
            db.collection("projects").document(projectId!!).set(projectData)
        }

        task.addOnSuccessListener {
            Toast.makeText(requireContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }.addOnFailureListener {
            btnRegister.isEnabled = true
            btnRegister.text = "저장"
            Toast.makeText(requireContext(), "실패하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
