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

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        initGoogleSignInClient()

        forceSignOut() // 앱이 켜질 때마다 기존 세션 강제 로그아웃

        initClick()
    }

    private fun forceSignOut() {
        // 1. 파이어베이스 로그아웃
        auth.signOut()

        // 2. 구글 클라이언트 로그아웃 (이걸 해야 계정 선택창이 다시 뜹니다)
        mGoogleSignInClient.signOut().addOnCompleteListener {
            Log.d("Login", "기존 구글 세션 삭제 및 로그아웃 완료")
        }
    }

    private fun initClick() {
        binding.btnGoogleLogin.setOnClickListener {
            startLoginGoogle()
        }
    }

    private fun initGoogleSignInClient() {
        // google-services.json에서 확인한 실제 Web Client ID를 직접 입력하여 리소스 인식 오류를 해결합니다.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1025195518832-nqhdmr1fctffrti7edst6tadlb5birno.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun startLoginGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        googleLoginResult.launch(signInIntent)
    }

    private val googleLoginResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                onError(e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    saveUserToFirestore(user)
                } else {
                    onError(task.exception)
                }
            }
    }

    private fun saveUserToFirestore(user: FirebaseUser?) {
        if (user == null) return
        
        val db = Firebase.firestore
        val userMap = hashMapOf(
            "uid" to user.uid,
            "name" to user.displayName,
            "email" to user.email,
            "profileUrl" to user.photoUrl.toString()
        )

        db.collection("users").document(user.uid)
            .set(userMap)
            .addOnSuccessListener {
                onLoginCompleted(user.uid, user.email)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "유저 정보 저장 실패", e)
                onLoginCompleted(user.uid, user.email)
            }
    }

    private fun onLoginCompleted(userId: String?, email: String?) {
        Toast.makeText(this, "로그인 성공: $email", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onError(error: Exception?) {
        Toast.makeText(this, "로그인 실패", Toast.LENGTH_SHORT).show()
        Log.e("Login", "구글 로그인 실패", error)
    }
}
