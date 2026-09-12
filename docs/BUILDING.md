<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Building From Source

1. Yêu cầu

Git

Android Studio phù hợp với Android Gradle Plugin của project

JDK 17+

Android SDK Platform 37

Internet cho lần resolve dependency đầu tiên

2. Clone

git clone https://github.com/trungdtt123-cmd/FaceAccess_v2.git
cd FaceAccess_v2

Không cần chỉnh sửa thủ công source/header để build.

3. Kiểm thử và build

Windows:

.\gradlew clean
.\gradlew testDebugUnitTest
.\gradlew assembleDebug

Linux/macOS:

chmod +x gradlew
./gradlew clean
./gradlew testDebugUnitTest
./gradlew assembleDebug

4. Kết quả

app/build/outputs/apk/debug/app-debug.apk

5. Cài lên thiết bị qua ADB

adb install -r app/build/outputs/apk/debug/app-debug.apk

Sau khi cài, bật Accessibility Service cho FaceAccess và cấp Camera/Notification khi Android yêu cầu.

6. Lưu ý reproducibility

Không commit local.properties.

Không commit output trong build/.

Dependency version nằm trong app/build.gradle.kts và gradle/libs.versions.toml.

Gradle Wrapper được pin trong gradle/wrapper/gradle-wrapper.properties.