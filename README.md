# 📱 AndroidLab: High-Performance Project Showcase Platform
> **Madcamp 2025 Winter - Project 2**  
> 모던 안드로이드 아키텍처와 실시간 클라우드 기술을 결합하여 설계된 전문 프로젝트 전시 및 소셜 인터랙션 플랫폼입니다.

---

## 🛠 Engineering & Tech Stack

### **Modern Android Ecosystem**
- **Language**: Kotlin (Coroutines, High-order Functions)
- **Architecture**: Fragment-based Single Activity Architecture
- **UI Framework**: XML with **ViewBinding**, **Material Design 3 (M3)**
- **Jetpack Components**: ViewModel(Planned), Fragment KTX, Navigation, Activity Result API

### **Cloud & Infrastructure**
- **Database**: **Firebase Cloud Firestore** (Real-time NoSQL)
- **Authentication**: **Firebase Auth** (Google OAuth 2.0 Integration)
- **Media Management**: **Cloudinary Android SDK** (Cloud-native Image Pipeline)
- **Image Loading**: **Glide** (Efficient Caching & Transformation)

---

## 💎 Key Technical Implementation

### **1. Intelligent Image Processing Pipeline**
- **Client-Side Optimization**: 대용량 원본 사진 업로드로 인한 지연을 방지하기 위해 업로드 전 **Bitmap 리사이징 및 80% JPEG 압축** 알고리즘을 구현했습니다.
- **Hybrid Data Handling**: `RegisterFragment`에서 **로컬 Uri**와 **서버 URL**을 동시에 처리하는 지능형 로직을 구축하여, 수정 시 변경된 사진만 선별적으로 업로드함으로써 네트워크 자원을 최적화했습니다.
- **Cloudinary Integration**: 서버리스 환경에서 이미지 변환 및 호스팅을 처리하는 유연한 아키텍처를 구현했습니다.

### **2. Reactive Real-time Synchronization**
- **Event-Driven UI**: Firestore의 `addSnapshotListener`를 전방위적으로 활용했습니다. 데이터가 서버에 기록되는 즉시 모든 클라이언트에 이벤트가 전파되어, **새로고침 없이** 프로젝트 목록, 좋아요 수, 덧글이 실시간 갱신됩니다.
- **In-memory Dynamic Sorting**: 서버에 부하를 주지 않고 클라이언트 메모리 내에서 Kotlin의 `sortedByDescending` 함수를 이용해 **최신순/좋아요순** 정렬을 실시간으로 수행합니다.

### **3. Scalable Data Modeling**
- **Sub-collection Design**: 프로젝트 문서 하위에 `comments` 서브 컬렉션을 배치하는 계층형 설계를 통해 수천 개의 덧글이 달려도 상위 목록 로딩 속도에 영향을 주지 않는 성능적 격리(Isolation)를 실현했습니다.
- **Concurrency Control**: `FieldValue.arrayUnion()` 및 `arrayRemove()`와 같은 원자적(Atomic) 연산을 사용하여 여러 사용자가 동시에 '좋아요'를 누를 때 발생할 수 있는 데이터 정합성 문제를 원천 차단했습니다.

### **4. Advanced Fragment Reusability**
- **Dual-Mode Editor**: `RegisterFragment` 하나로 **[신규 등록]**과 **[기존 데이터 수정]** 모드를 완벽하게 통합했습니다. 
- `arguments` 기반의 모드 판별과 UI 상태 동적 변경 로직을 통해 코드 중복을 최소화하고 유지보수성을 극대화했습니다.

---

## 🚀 Full Features

### **1. Social Engagement**
- **Google Social Login**: Firebase Auth를 통한 원클릭 가입 및 프로필 연동.
- **Real-time Interaction**: 목록 및 상세 화면에서 즉각적인 하트(좋아요) 토글 기능.
- **Interactive Commenting**: 프로젝트별 독립적인 소통 공간 및 작성자 프로필 연동.

### **2. Content Discovery (UX)**
- **Dual Layout Engine**: 2열 격자(`GridView`)와 상세 카드(`ListView`) 간의 유연한 탐색 경험.
- **Media Slider**: `ViewPager2`를 활용한 몰입감 있는 이미지 슬라이딩 인터페이스.
- **Responsive Scrolling**: `NestedScrollView`를 통한 다양한 기기 해상도 최적 대응.

### **3. Personalized Management (My Page)**
- **Project Tracking**: 내가 등록한 프로젝트들만 모아보기 및 실시간 관리.
- **Edit/Delete Workflow**: 등록된 결과물의 실시간 수정 및 직관적인 삭제 플로우.

---

## 📂 Project Structure

```text
com.example.androidlab
├── models
│   ├── Project.kt         # 메인 프로젝트 데이터 스키마 (LikedBy 리스트 포함)
│   └── Comment.kt         # 서브 컬렉션용 덧글 데이터 스키마
├── ui
│   ├── grid               # 2-Column 그리드 인터페이스 및 정렬 로직
│   ├── list               # 고해상도 리스트 인터페이스
│   ├── register           # 이미지 압축/업로드 & 등록/수정 통합 로직
│   ├── detail             # 상세 정보, ViewPager2 슬라이더 & 실시간 덧글 시스템
│   └── mypage             # 내 프로젝트 관리 및 개인화 레이어
├── LoginActivity          # Google OAuth 인증 및 User Sync
├── MainActivity           # Fragment 흐름 제어 및 하단 네비게이션
└── SplashActivity         # Session 체크 및 자동 로그인 엔진
```

---

## 👥 Team Members
- **박새연** (Sae-yeon Park)
- **강승수** (Seung-su Kang)

---

## ⚙️ How to Run
1. Google Firebase 콘솔에서 `google-services.json`을 다운로드하여 `app/` 폴더에 삽입합니다.
2. `RegisterFragment.kt`의 `cloudinaryConfig`에 본인의 Cloud API Key를 입력합니다.
3. Android Studio에서 Gradle Sync 후 프로젝트를 실행합니다. (Target SDK 36 지원)

---
**Developed with Technical Excellence by Madcamp 2025 Winter Team.**
