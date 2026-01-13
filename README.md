# 📱 프로젝트 전시 앱 (Madcamp 2025 Winter)

**Madcamp 2025 Winter** 시즌 동안 개발된 프로젝트 전시 및 소셜 상호작용 애플리케이션입니다. 사용자들이 자신의 프로젝트와 여러 장의 사진을 등록하고, 다른 이들의 프로젝트를 둘러보며 '하트(좋아요)'를 통해 반응을 남길 수 있는 플랫폼입니다.

## 🚀 주요 기능

### 1. 계정 및 인증
- **Google 소셜 로그인**: Firebase Auth를 통해 복잡한 가입 절차 없이 구글 계정으로 즉시 이용 가능합니다.
- **자동 로그인**: 앱 실행 시 이전 로그인 상태를 확인하여 편리한 진입을 지원합니다.

### 2. 프로젝트 관리 (CRUD)
- **다중 이미지 업로드**: Cloudinary API를 연동하여 프로젝트당 최대 5장의 고화질 사진을 등록할 수 있습니다.
- **실시간 데이터 저장**: 작성한 제목, 설명, 팀원 정보가 Firestore NoSQL DB에 실시간으로 기록됩니다.

### 3. 탐색 및 정렬
- **Grid & List View**: 사용자 취향에 따라 격자 형태 또는 상세 리스트 형태로 목록을 탐색할 수 있습니다.
- **스마트 정렬**: 
  - **최신순**: 가장 최근에 등록된 프로젝트부터 확인.
  - **좋아요순**: 유저들에게 가장 인기가 많은 프로젝트 순으로 정렬.
- **실시간 동기화**: 새로운 글이 올라오거나 좋아요 수가 변하면 앱을 새로고침하지 않아도 즉시 화면에 반영됩니다.

### 4. 소셜 상호작용
- **하트(좋아요) 기능**: 목록이나 상세 화면에서 즉시 하트를 누르거나 취소할 수 있습니다.
- **이미지 슬라이더**: 상세 페이지에서 `ViewPager2`를 통해 업로드된 여러 장의 사진을 슬라이드로 감상할 수 있습니다.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Android XML (ViewBinding)
- **Database**: Firebase Cloud Firestore
- **Authentication**: Firebase Auth (Google Login)
- **Image Hosting**: Cloudinary
- **Libraries**:
  - **Glide**: 효율적인 네트워크 이미지 캐싱 및 로딩
  - **Cloudinary Android SDK**: 클라우드 이미지 업로드 및 관리
  - **RecyclerView & ViewPager2**: 유연한 목록 및 슬라이더 구현
  - **Material Design 3**: 현대적인 UI 컴포넌트 적용

## 📂 Project Structure

```text
com.example.androidlab
├── models
│   └── Project.kt         # 데이터 모델 (id, title, imageUrls, likedBy 등)
├── ui
│   ├── grid               # 그리드 목록 화면 (Fragment, Adapter)
│   ├── list               # 리스트 목록 화면 (Fragment, Adapter)
│   ├── register           # 프로젝트 등록 및 이미지 업로드 (Fragment, Adapter)
│   └── detail             # 상세 정보 및 하트 기능 (Fragment, PagerAdapter)
├── MainActivity           # 하단 네비게이션 및 프래그먼트 제어
├── LoginActivity          # 구글 로그인 및 유저 정보 Firestore 저장 로직
└── SplashActivity         # 앱 시작 화면 및 로그인 상태 체크
```

## ⚙️ 설정 및 실행 방법

1. **Firebase 설정**: 
   - `app/google-services.json` 파일을 발급받아 `app/` 폴더에 배치해야 합니다.
2. **Cloudinary 설정**: 
   - `RegisterFragment.kt` 내 `cloudinaryConfig`와 `unsigned` 프리셋 이름(`madcamp_winter_2025`)이 설정되어 있습니다.
3. **Gradle Sync**: 
   - Android Studio에서 프로젝트를 연 후 상단의 Elephant 아이콘을 눌러 라이브러리를 동기화합니다.

---

### 💻 개발 상태
- **진행도**: 핵심 기능(로그인, 다중 이미지 업로드, 실시간 DB 연동, 정렬, 좋아요) 구현 완료.
- **개발 환경**: Android Studio Koala | Target SDK 36 | Min SDK 24
