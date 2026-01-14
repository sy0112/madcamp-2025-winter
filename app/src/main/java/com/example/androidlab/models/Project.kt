package com.example.androidlab.models

/**
 * 프로젝트 정보를 담는 데이터 모델 클래스입니다.
 * Firestore의 데이터를 이 객체 형식으로 자동으로 변환하거나,
 * 반대로 이 객체를 Firestore에 저장할 때 사용됩니다.
 */
data class Project(
    val id: String = "",            // Firestore 문서의 고유 ID
    val title: String = "",         // 프로젝트 제목
    val description: String = "",   // 프로젝트 상세 설명
    val members: String = "",       // 참여 팀원 명단
    val githubUrl: String = "",     // 🌟 추가: 깃허브 주소 링크
    val ownerUid: String = "",      // 등록한 유저의 고유 식별자(UID)
    val ownerEmail: String = "",    // 등록한 유저의 이메일 주소
    val imageUrls: List<String> = emptyList(), // 이미지 URL 리스트 (DB 필드명과 일치)
    val createdAt: Long = 0,        // 등록 시간 (타임스탬프)
    val likedBy: List<String> = emptyList() // 좋아요를 누른 유저들의 UID 리스트
)
