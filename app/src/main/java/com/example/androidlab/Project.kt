package com.example.androidlab.models

data class Project(
    val id: String = "",            // Firestore 문서 ID
    val title: String = "",
    val description: String = "",
    val members: String = "",
    val ownerUid: String = "",
    val createdAt: Long = 0
)