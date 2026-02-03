AGENTS.md
​1. Environment & Path Configuration (FIXED - DO NOT CHANGE)
​This project is developed On-Device (Android Environment). Standard desktop paths (Mac/Windows/Linux) do not apply.
​IDE: Android Code Studio / AndroidIDE (AndroidCSOfficial v1.0.0+gh.r3)
​Device Arch: arm64-v8a
​Java Home: OpenJDK 17.0.16
​SDK Location: /data/user/0/com.tom.rv2ide/files/home/android-sdk/
​NDK Location: Inside SDK folder (Version 28.2.13676358)
​2. Build Toolchain Versions (STRICT)
​You MUST respect these versions strictly. Do not downgrade or suggest incompatible versions.
​Kotlin Version: 2.1.0
​Compile SDK: 35
​Build Tools: 35.0.1
​NDK Version: 28.2.13676358
​CMake Version: 4.1.1 (Installed & Verified)
​Android Gradle Plugin (AGP): (Must be 8.4.0, 8.5.0, or newer to support SDK 35
​Gradle Wrapper: 8.13-bin or 9.0.0-bin
​3. Project Identity & Structure (DYNAMIC)
​App Name: FlyMotion
​Package ID: com.fly.motion
​Module Structure:
- **Module Structure:**
    - `app` (Main Module):
        - **Logic Location:** All Kotlin files are in `app/src/main/kotlin/`.
        - **Native Location:** All C++ files are in `app/src/main/cpp/`.
        - **Build Scripts:** Controlled by `app/build.gradle.kts`.
        - **Manifest:** `app/src/main/AndroidManifest.xml` controls permissions & services.
​Target Libraries:
 - `native-lib`: Contains **Core Video Stabilisation Logic** & ** Video Stabilisation Processing Algorithms**.
      (This is the performance-critical part of the app, handled by JNI).
​Third Party Native Libs:
- **Third Party Native Libs:**
    - **OpenCV:** (Used for editing, video stabilisation).
      - Location: `app/src/main/cpp/libs/opencv`
      - CMake Config: Linked via `add_library(lib_opencv SHARED IMPORTED)`.

​5. Development Constraints
​FileSystem: The project resides in independent Terminal/App private storage. Absolute paths must use the SDK Location defined above.
​Build System: Ninja is implicitly used by CMake 4.1.1.
​Known Issues:
​...