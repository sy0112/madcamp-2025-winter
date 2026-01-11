package com.example.androidlab.models

data class Project(
    val id: String = "",            // Firestore 문서 ID
    val title: String = "",
    val description: String = "",
    val members: String = "",
    val ownerUid: String = "",
    val createdAt: Long = 0,
    val images: List<Int> = emptyList() // 이미지 리스트 필드 추가
)
