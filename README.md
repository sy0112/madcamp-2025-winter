# 📱 (Madcamp 2025 Winter)

**Madcamp 2025 Winter 시즌 동안 개발된 프로젝트 전시 및 관리 애플리케이션입니다. 사용자들이 자신의 프로젝트를 등록하고, 다른 이들의 프로젝트를 격자(Grid) 및 리스트(List) 형태로 둘러볼 수 있는 기능을 제공합니다.

## 🚀 주요 기능

- **Google 계정 로그인**: Firebase Auth를 통한 간편한 구글 소셜 로그인.
- **프로젝트 등록**: 새로운 프로젝트 정보(제목, 팀원, 이미지 등)를 Firestore DB에 실시간 저장.
- **다양한 뷰 모드**: 
  - **Grid View**: 프로젝트를 시각적으로 한눈에 확인.
  - **List View**: 상세한 리스트 형태로 프로젝트 목록 탐색.
- **하단 네비게이션**: 메인 화면에서 등록, 그리드, 리스트 화면으로의 자유로운 이동.

## 🛠 Tech Stack

- **Language**: Kotlin
- **UI Framework**: Android XML (ViewBinding), Jetpack Compose (Partial)
- **Architecture**: Fragment-based Navigation with BottomNavigationView
- **Database & Auth**: Firebase (Firestore, Authentication)
- **Library**:
  - RecyclerView & ViewPager2
  - Google Play Services Auth
  - Material Design Components

## 📂 Project Structure

```text
com.example.androidlab
├── ui
│   ├── grid       # 프로젝트 그리드 화면 (GridFragment)
│   ├── list       # 프로젝트 리스트 화면 (ListFragment)
│   ├── register   # 프로젝트 등록 화면 (RegisterFragment)
│   └── detail     # 프로젝트 상세 화면 (DetailFragment)
├── Project        # 데이터 모델 클래스
├── ProjectAdapter # RecyclerView 어댑터
├── MainActivity   # 하단 네비게이션 및 프래그먼트 제어
├── LoginActivity  # 구글 로그인 처리
└── SplashActivity # 앱 시작 화면 및 자동 로그인 체크
```

## ⚙️ 실행 및 설정 주의사항

1. **Firebase 설정**: `app/google-services.json` 파일이 필요합니다.
2. **SDK 버전**: 최신 AndroidX 라이브러리 호환을 위해 `compileSdk` 및 `targetSdk` 버전이 **36**으로 설정되어 있습니다.
3. **Web Client ID**: 구글 로그인 연동을 위해 `LoginActivity` 내에 Firebase 콘솔에서 발급받은 `Web Client ID`가 직접 설정되어 있습니다.

## 📝 설치 및 실행 방법

1. 이 저장소를 Clone 합니다.
   ```bash
   git clone https://github.com/your-repo/androidlab.git
   ```
2. Android Studio에서 프로젝트를 엽니다.
3. `google-services.json` 파일을 `app/` 폴더에 배치합니다.
4. Gradle Sync를 완료한 후 `app`을 실행합니다.

---

### 💻 개발 상태
- **진행도**: 구글 로그인 연동 완료 및 Firestore 기본 데이터 입출력 구현 중.
- **개발 환경**: Android Studio Jellyfish | SDK 36
