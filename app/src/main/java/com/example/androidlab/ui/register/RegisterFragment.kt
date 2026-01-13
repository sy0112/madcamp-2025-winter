package com.example.androidlab.ui.register

import android.net.Uri
// 안드로이드에서 리소스의 위치를 나타내는 주소 체계 = Uri
// 사용자가 갤러리에서 사진을 선택하면 안드로이드 시스템은 사진 파일 그 자체를 주는 것이 아니라, 주소(Uri)를 줌.

import android.os.Bundle
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * 프로젝트 정보를 입력하고 사진을 업로드하여 등록하는 화면입니다.
 */
class RegisterFragment : Fragment(R.layout.fragment_register) {
    // Fragment를 상속함.
    // R은 res 전체를 의미.
    // fragment_register.xml을 가져온다.

    // Firebase Firestore 및 Auth 인스턴스 초기화
    private val db = Firebase.firestore
    // firebase의 DB 가져오기
    private val auth = Firebase.auth
    // Firebase Authentication 가져오기

    // 사용자가 선택한 이미지들의 Uri 리스트
    // 코틀린의 리스트에는 크게 두 종류
    // 1) listof (읽기 전용)
    // 2) mutableListof(변경 가능)
    private val selectedImageUris = mutableListOf<Uri>()

    //lateinit 나중에 초기화할 변수
    // 안드로이드 뷰는 onViewCreated 같은 특정 시점이 되어야 찾을 수 있다.
    // recycleview는 껍데기. 안에 어떻게 사진을 넣고, 어떻게 배치할지는 adapter가 한다.
    private lateinit var rvImages: RecyclerView
    //ImageSelectAdapter에 대해서 import가 없는 이유?
    // 두 클래스가 같은 패키지에 속해져 있기 때문이다.
    private lateinit var imageAdapter: ImageSelectAdapter

    // Cloudinary 접속 설정 정보
    private val cloudinaryConfig = mapOf(
        "cloud_name" to "dlotgejuu",
        "api_key" to "398175322742183",
        "api_secret" to "j0dWzx_Ke1NSOEmA1pVrWvYY5tE"
    )


    //fragment가 생성될 때 가장 먼저 실행 되는 단계 중 하나.
    // 보통 onCreate에 필요한 데이터나 외부 라이브러리를 준비하는 코드 작성
    // savedInstanceState: Bundle?는 안드로이드에서 화면이 가로/세로로 회전하거나 시스템 메모리 부족으로 앱이 잠시 종료되었다가 다시 살아날때 이전의 상태를 기억하는 메모장
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // 상속
        // Cloudinary MediaManager 초기화 (이미 되어있지 않은 경우에만)
        try {
            MediaManager.init(requireContext(), cloudinaryConfig)
        } catch (e: IllegalStateException) {
            // 이미 초기화된 경우 무시
        }
    }

    // 사진첩에서 여러 장의 사진(최대 5장)을 선택하기 위한 런처 설정
    private val pickMultipleMedia = registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
        if (uris.isNotEmpty()) {
            // selectedImageUris 는 일종의 리스트
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            imageAdapter.notifyDataSetChanged() // 선택된 이미지 목록 갱신
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }
    // 화면 ui가 완전히 그려진 후 호출되는 함수
    // ui는 User Interface 로 사용자가 앱을 사용할 때 눈으로 보고 손으로 만지는 것
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState) // 상속

        // 뷰 연결
        // viewfindviewByID 는 XML 디자인 파일에 있는 뷰를 코틀린 코드의 변수와 연결.
        // 아까 앞에서 R.layout.fragment_register 이거 함수의 요소로 가져옴.
        rvImages = view.findViewById(R.id.rvImages) // XML의 RecyclerView를 찾아와서 변수에 할당 (선택된 사진 목록 표시용)
        val btnSelectImage = view.findViewById<Button>(R.id.btnSelectImage) // "사진 선택" 버튼 찾기
        val etTitle = view.findViewById<EditText>(R.id.etTitle) // "프로젝트 제목" 입력창 찾기
        val etDescription = view.findViewById<EditText>(R.id.etDescription) // "프로젝트 설명" 입력창 찾기
        val etMembers = view.findViewById<EditText>(R.id.etMembers) // "팀원 이름" 입력창 찾기
        val btnRegister = view.findViewById<Button>(R.id.btnRegister) // "등록" 버튼 찾기

        // 가로 스크롤 형태의 사진 선택 목록 설정
        imageAdapter = ImageSelectAdapter(selectedImageUris)
        
        // 가로 스크롤
        rvImages.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        
        rvImages.adapter = imageAdapter

        // 사진 선택 버튼 클릭 시 사진첩 열기
        btnSelectImage.setOnClickListener {
            pickMultipleMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 등록 버튼 클릭 시 데이터 검증 및 업로드 시작
        btnRegister.setOnClickListener {
            val currentUser = auth.currentUser
            // 로그인 안 되어 있을 지.
            if (currentUser == null) {
                Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val title = etTitle.text.toString().trim() //  trim은 문자열의 앞과 뒤에 있는 불필요한 공백 제거
            val description = etDescription.text.toString().trim() 
            val members = etMembers.text.toString().trim()

            // 필수 입력값 확인
            if (title.isEmpty() || description.isEmpty() || members.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 사진이 있으면 업로드 후 저장, 없으면 바로 텍스트 데이터만 저장
            if (selectedImageUris.isNotEmpty()) {
                uploadImagesAndSaveData(currentUser.uid, currentUser.email, title, description, members)
            } else {
                saveProjectData(currentUser.uid, currentUser.email, title, description, members, emptyList())
            }
        }
    }

    /**
     * 선택된 이미지들을 Cloudinary 서버에 순차적으로 업로드합니다.
     */
    private fun uploadImagesAndSaveData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String
    ) {
        val uploadedUrls = mutableListOf<String>()
        var uploadCount = 0

        selectedImageUris.forEach { uri ->
            MediaManager.get().upload(uri)
                .unsigned("madcamp_winter_2025") // 사전에 설정한 Unsigned Upload Preset 사용
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}
                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                        // 업로드 성공 시 반환된 secure_url(이미지 주소)을 리스트에 저장
                        val imageUrl = resultData?.get("secure_url") as? String
                        if (imageUrl != null) {
                            uploadedUrls.add(imageUrl)
                        }
                        uploadCount++
                        
                        // 모든 사진의 업로드가 완료되면 최종 데이터를 Firestore에 저장
                        if (uploadCount == selectedImageUris.size) {
                            saveProjectData(uid, email, title, description, members, uploadedUrls)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        uploadCount++
                        Log.e("Cloudinary", "Upload error: ${error?.description}")
                        
                        // 일부 업로드에 실패하더라도 진행된 건들만이라도 저장하기 위해 확인
                        if (uploadCount == selectedImageUris.size) {
                            saveProjectData(uid, email, title, description, members, uploadedUrls)
                        }
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                }).dispatch()
        }
    }

    /**
     * 업로드된 이미지 URL 리스트와 함께 프로젝트 최종 데이터를 Firestore에 저장합니다.
     * 
     * @param uid 프로젝트를 작성한 사용자의 고유 ID (Firebase Auth에서 가져옴)
     * @param email 프로젝트를 작성한 사용자의 이메일 주소
     * @param title 사용자가 입력한 프로젝트 제목
     * @param description 사용자가 입력한 프로젝트 설명
     * @param members 사용자가 입력한 팀원 명단
     * @param imageUrls Cloudinary 서버 업로드 성공 후 받은 이미지 주소(URL)들의 목록
     * 근데 여기는 없음
     */

    // 이미지 없을 때 호출되는 함수
    private fun saveProjectData(
        uid: String,
        email: String?,
        title: String,
        description: String,
        members: String,
        imageUrls: List<String>
    ) {
        // 1. 서버에 저장할 데이터를 하나의 꾸러미(HashMap)로 만듭니다.
        // 키(Key)는 서버에서 사용할 이름이고, 값(Value)은 실제 저장할 데이터입니다.
        val projectData = hashMapOf(
            "ownerUid" to uid,             // 작성자 구분용 ID
            "ownerEmail" to email,         // 작성자 연락처용 이메일
            "title" to title,             // 프로젝트 제목
            "description" to description, // 상세 내용
            "members" to members,         // 함께한 팀원들
            "imageUrls" to imageUrls,     // 사진 주소 리스트 (문자열 리스트)
            "createdAt" to System.currentTimeMillis() // 등록 시간 기록 (현재 시각의 숫잣값)
        )

        // 2. Firestore의 "projects"라는 이름의 보관함(Collection)에 접근합니다.
        db.collection("projects")
            // db = firebase
            // 3. 위에서 만든 데이터 꾸러미(projectData)를 새로운 문서로 추가(add)합니다.
            .add(projectData)
            // 4. 저장이 성공했을 때 실행될 코드 블록입니다.
            .addOnSuccessListener {
                // 사용자에게 완료 알림(Toast)을 띄웁니다.
                Toast.makeText(requireContext(), "프로젝트가 등록되었습니다.", Toast.LENGTH_SHORT).show()
                
                // 5. 등록이 끝났으므로 화면을 메인 목록 화면(GridFragment)으로 전환합니다.
                // parentFragmentManager를 통해 현재 화면을 갈아끼웁니다.
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, GridFragment())
                    .commit()
            }
            // 6. 예기치 못한 에러로 저장이 실패했을 때 실행될 코드 블록입니다.
            .addOnFailureListener { e ->
                // 에러 원인을 로그캣(Logcat)에 기록하여 개발자가 확인할 수 있게 합니다.
                Log.e("Firestore", "저장 에러", e)
                // 사용자에게 실패했음을 알립니다.
                Toast.makeText(requireContext(), "등록 실패", Toast.LENGTH_SHORT).show()
            }
    }
}
