package com.example.androidlab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.androidlab.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

/**
 * [LoginActivity] 클래스 설명:
 * 구글 계정을 이용한 로그인 기능을 담당하는 액티비티입니다.
 * 구글 로그인이 성공하면 Firebase Auth와 연동하고, 유저 정보를 Firestore에 저장합니다.
 */
class LoginActivity : AppCompatActivity() {

    // 뷰 바인딩: XML의 위젯들을 id로 직접 참조하기 위한 도구
    private lateinit var binding: ActivityLoginBinding
    // 구글 로그인 클라이언트: 구글 로그인 옵션과 기능을 관리
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    // Firebase 인증 객체: 실제 로그인을 처리하고 유저 정보를 관리
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. 뷰 바인딩 초기화 및 화면 설정
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Firebase Auth 인스턴스 가져오기
        auth = Firebase.auth
        
        // 3. 구글 로그인 클라이언트 설정
        initGoogleSignInClient()
        
        // 4. 클릭 이벤트 연결
        initClick()
    }

    /**
     * 클릭 리스너들을 초기화합니다.
     */
    private fun initClick() {
        binding.btnGoogleLogin.setOnClickListener { // btnGoogleLogin 버튼이 눌렸을 때 무엇을 할 것인지.
            // 로그인 전 기존 세션을 로그아웃하여 항상 계정 선택창이 뜨도록 함
            mGoogleSignInClient.signOut().addOnCompleteListener { // 현재 기기에 남아있는 구글 로그인 세션 제거
                startLoginGoogle() // 실제 로그인 flow 시작
            }
        }
    }

    /**
     * 구글 로그인에 필요한 옵션(GSO)을 설정하고 클라이언트를 초기화합니다.
     */
    private fun initGoogleSignInClient() {
        //requestIdToken에는 Firebase 콘솔의 웹 클라이언트 ID를 넣어야 합니다.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1025195518832-nqhdmr1fctffrti7edst6tadlb5birno.apps.googleusercontent.com")  // 이건 google oAuth 2.0 Client ID
            //client는 내가 만든 앱
            //  firebase 인증에 필요한 ID 토큰을 요청
            // firebase 콘솔 -> authentication -> Google 로그인
            .requestEmail() // 로그인 성공 시 이메일 정보 요청
            .build() // 위에 설정한 옵션들을 하나의 객체로
            
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        // 실제 구글 로그인 기능을 수행하는 클라이언트 생성
    }

    /**
     * 실제 구글 로그인 화면(시스템 다이얼로그)을 실행합니다.
     */
    private fun startLoginGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleLoginResult.launch(signInIntent)
    }

    /**
     * 구글 로그인 화면에서 돌아온 결과를 처리하는 콜백입니다.
     */
    private val googleLoginResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 로그인 창에서 성공적으로 돌아왔는지 확인
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                // 인텐트 데이터에서 구글 계정 정보를 추출
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                
                // 추출한 ID 토큰을 사용하여 Firebase 인증 진행
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.e("Login", "구글 로그인 에러 코드: ${e.statusCode}")
                onError(e)
            }
        } else {
            Log.e("Login", "구글 로그인 취소 또는 실패: ${result.resultCode}")
        }
    }

    /**
     * 구글에서 받은 토큰을 사용하여 Firebase 인증 시스템에 로그인합니다.
     */
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Firebase 로그인 성공 시 현재 유저 정보를 Firestore에 저장 시도
                    val user = auth.currentUser
                    saveUserToFirestore(user)
                } else {
                    Log.e("Login", "Firebase 연동 실패", task.exception)
                    onError(task.exception)
                }
            }
    }

    /**
     * 로그인된 유저의 정보를 Firestore 데이터베이스의 "users" 컬렉션에 저장합니다.
     */
    private fun saveUserToFirestore(user: FirebaseUser?) {
        if (user == null) return
        
        val db = Firebase.firestore
        // 저장할 유저 정보 맵 구성
        val userMap = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "profileUrl" to user.photoUrl.toString()
        )

        // "users" 컬렉션에 UID를 문서 이름으로 하여 정보를 저장(또는 덮어쓰기)
        db.collection("users").document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                onLoginCompleted(user.uid, user.email)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "유저 정보 저장 실패", e)
                // DB 저장이 실패하더라도 로그인은 된 상태이므로 다음 화면으로 넘김
                onLoginCompleted(user.uid, user.email) 
            }
    }

    /**
     * 모든 로그인 과정이 완료되었을 때 호출됩니다. 메인 화면으로 이동합니다.
     */
    private fun onLoginCompleted(userId: String?, email: String?) {
        Toast.makeText(this, "환영합니다! $email", Toast.LENGTH_SHORT).show()
        // 메인 화면 실행
        startActivity(Intent(this, MainActivity::class.java))
        // 로그인 화면은 스택에서 제거하여 뒤로가기 시 돌아오지 않게 함
        finish()
    }

    /**
     * 로그인 과정 중 에러가 발생했을 때 호출됩니다.
     */
    private fun onError(error: Exception?) {
        Toast.makeText(this, "로그인 실패: ${error?.message}", Toast.LENGTH_SHORT).show()
        Log.e("Login", "상세 에러 내용", error)
    }
}
