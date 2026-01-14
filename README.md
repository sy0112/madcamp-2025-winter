# 🪿거기(Geogi)
> **Madcamp 2025 Winter - Project 1**  
> 거위의 기록 : 프로젝트를 공유하고 소통하는 개발자들의 소셜 네트워크 플랫폼

![로고](./app/logo.png)


[Notion](https://www.notion.so/madcamp/Geogi-2e85a1b83557808a8dd5d0fa55272ff1?source=copy_link)
---

## 🛠 Engineering & Tech Stack

### **Modern Android Ecosystem**
- **Language**: Kotlin (Coroutines, High-order Functions)
- **Architecture**: Fragment-based Single Activity Architecture
- **UI Framework**: XML with **ViewBinding**, **Material Design 3 (M3)**
- **Jetpack Components**: Fragment KTX, Navigation, Activity Result API

### **Cloud & Infrastructure**
- **Database**: **Firebase Cloud Firestore** (Real-time NoSQL)
- **Authentication**: **Firebase Auth** (Google OAuth 2.0 Integration)
- **Media Management**: **Cloudinary Android SDK** (Cloud-native Image Pipeline)
- **Image Loading**: **Glide** (Efficient Caching & Transformation)

---

## 💎 Key Technical Implementation

### **1. Intelligent Content Editor (Dual-Mode)**
- **Unified Workflow**: `RegisterFragment` 하나로 **신규 등록**과 **기존 데이터 수정** 로직을 완벽하게 통합했습니다. 
- **Atomic Updates**: `SetOptions.merge()`를 사용하여 수정 시 기존 데이터를 보존하면서 변경된 필드만 안전하게 동기화합니다.

### **2. Reactive Real-time Synchronization**
- **Live Updates**: Firestore의 `addSnapshotListener`를 활용하여 새로고침 없이도 좋아요 수, 프로젝트 목록이 전 클라이언트에 즉시 갱신됩니다.
- **Dynamic In-memory Sorting**: 사용자 선택에 따라 **최신순/좋아요순** 정렬을 클라이언트 사이드에서 실시간으로 처리합니다.

### **3. Immersive User Interaction & Animations**
- **Explosive Feedback**: 프로젝트 등록 성공 시 **Konfetti 라이브러리**를 통한 화려한 시각적 축하 효과를 제공합니다.
- **Playful Interactions**: 하트 클릭 시 **360도 공중제비(RotationY)** 애니메이션과 화면 중앙에서 거대 하트가 터지는 **Heart Pop 애니메이션**을 구현하여 정적인 앱에 생동감을 불어넣었습니다.

### **4. Media & Session Management**
- **Cloud-Native Image Pipeline**: Cloudinary SDK를 연동하여 다중 이미지의 안정적인 업로드 및 클라우드 호스팅을 처리합니다.
- **Persistent Session Engine**: `SplashActivity`에서의 세션 검증 로직을 통해 로그아웃 전까지 사용자의 로그인 상태를 완벽하게 유지합니다.

---

## 🚀 Key Features

- **Google Social Login**: 번거로운 절차 없는 간편 인증 시스템.
- **Dual Discovery Mode**: 시각적인 그리드 뷰와 정보 중심의 리스트 뷰 선택 지원.
- **Project Management**: 자신이 등록한 프로젝트에 대한 완전한 제어권(수정/삭제) 제공.
- **Personalized Curation**: 내가 등록한 프로젝트 및 '좋아요'를 누른 프로젝트 모아보기.

---

## 📂 Project Structure

```text
com.example.androidlab
├── models
│   └── Project.kt         # 메인 프로젝트 데이터 모델 (LikedBy 연동)
├── ui
│   ├── grid               # 2-Column 그리드 인터페이스 및 정렬 로직
│   ├── list               # 고해상도 리스트 인터페이스
│   ├── register           # Cloud 업로드 & 등록/수정 통합 로직
│   ├── detail             # ViewPager2 슬라이더 & 하트 팝 애니메이션
│   └── mypage             # 개인화 관리 레이어 (내 프로젝트, 좋아요 목록)
├── LoginActivity          # Google OAuth 인증 인터페이스
├── MainActivity           # Fragment 흐름 제어 및 내비게이션
└── SplashActivity         # 자동 로그인 및 세션 매니지먼트
```

---

## 👥 Team Members
- **박새연** (Sae-yeon Park)
- **강승수** (Seung-su Kang)

---

## ⚙️ How to Run
1. Firebase 콘솔에서 `google-services.json`을 발급받아 `app/` 폴더에 삽입합니다.
2. `RegisterFragment.kt`의 `cloudinaryConfig`에 본인의 API Key를 설정합니다.
3. Android Studio에서 Gradle Sync 후 실행합니다. (Target SDK 36 지원)

---
**Developed with Passion and Technical Excellence by Madcamp 2025 Winter Team.**
