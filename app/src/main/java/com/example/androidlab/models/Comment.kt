package com.example.androidlab.models

/**
 * 덧글 정보를 담는 데이터 모델 클래스입니다.
 * 프로젝트 상세 화면에서 각 프로젝트에 달린 의견을 저장하고 표시할 때 사용합니다.
 */
data class Comment(
    val id: String = "",          // 덧글의 고유 문서 ID
    val userId: String = "",      // 작성한 유저의 고유 식별자(UID)
    val userName: String = "",    // 작성한 유저의 이름 (화면 표시용)
    val userProfileUrl: String? = null, // (선택사항) 작성자의 프로필 이미지 주소
    val content: String = "",     // 덧글 내용
    val createdAt: Long = 0       // 작성 시간 (타임스탬프)
)
