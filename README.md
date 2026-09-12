FaceAccess v2

FaceAccess v2 là ứng dụng Android mã nguồn mở hỗ trợ điều khiển thiết bị bằng cử chỉ khuôn mặt. Hệ thống sử dụng MediaPipe Face Landmarker để phân tích khuôn mặt theo thời gian thực, sau đó kết hợp chuẩn hóa, hiệu chỉnh thích nghi, bộ nhận diện cử chỉ và Android Accessibility Service để thực hiện thao tác trên thiết bị.

Mục tiêu của dự án là cung cấp một phương thức tương tác rảnh tay, có thể cá nhân hóa và hoạt động trên nhiều thiết bị Android.

Thành viên

Hoàng Thị Kiều Anh

Phạm Văn Dượng

Đặng Quốc Trung

Chức năng chính

Nhận diện quay đầu trái/phải (Yaw), nhìn lên/xuống (Pitch), nghiêng đầu (Roll).

Nhận diện nhắm hai mắt, mở miệng và mở miệng hai lần.

4 chế độ điều khiển: Điều hướng, Media, Hỗ trợ và Con trỏ.

Điều khiển con trỏ và thao tác hệ thống thông qua Accessibility Service.

Hỗ trợ luồng gọi điện và chọn SIM trên thiết bị có giao diện chọn SIM.

Hiệu chỉnh thích nghi theo từng người dùng và lưu cấu hình để dùng lại.

Hoạt động theo dõi nền bằng foreground service khi người dùng bật theo dõi.

AI được sử dụng như thế nào?

FaceAccess sử dụng MediaPipe Face Landmarker, một mô hình ML đã được huấn luyện sẵn, để suy luận từ camera và cung cấp landmarks, blendshape scores và facial transformation matrices. FaceAccess không huấn luyện lại trọng số của MediaPipe.

Phần do nhóm phát triển nằm ở tầng sau mô hình: trích xuất Yaw/Pitch/Roll và tín hiệu mắt/miệng, chuẩn hóa dữ liệu, hiệu chỉnh thích nghi theo người dùng, state machine nhận diện cử chỉ, điều phối theo chế độ và thực thi thao tác Accessibility.

Xem chi tiết tại docs/AI_ARCHITECTURE.md.

Kiến trúc tổng quát

CameraX
↓
MediaPipe Face Landmarker
↓
Landmarks / Blendshapes / Transformation Matrix
↓
Trích xuất Yaw / Pitch / Roll / mắt / miệng
↓
Chuẩn hóa + hiệu chỉnh thích nghi
↓
Nhận diện cử chỉ + state machine
↓
Điều phối chế độ
↓
Android Accessibility Service

Xem thêm tại docs/ARCHITECTURE.md.

Công nghệ

Kotlin

Android SDK / AndroidX

CameraX

MediaPipe Tasks Vision / Face Landmarker

Android Accessibility Service

Material Components

uCrop

Lottie

Gradle

Danh sách dependency và giấy phép liên quan được ghi tại THIRD_PARTY_NOTICES.md.

Yêu cầu môi trường

Android Studio tương thích với Android Gradle Plugin của dự án

JDK 17 trở lên

Android SDK Platform 37

Android API 26 trở lên

Kết nối Internet trong lần build đầu để Gradle tải dependency

Build từ mã nguồn

Clone repository:

git clone https://github.com/trungdtt123-cmd/FaceAccess_v2.git
cd FaceAccess_v2

Trên Windows PowerShell:

.\gradlew clean
.\gradlew testDebugUnitTest
.\gradlew assembleDebug

Trên Linux/macOS:

chmod +x gradlew
./gradlew clean
./gradlew testDebugUnitTest
./gradlew assembleDebug

APK debug sau khi build thành công:

app/build/outputs/apk/debug/app-debug.apk

Hướng dẫn chi tiết: docs/BUILDING.md.

Cài đặt và chạy

Cài APK lên thiết bị Android API 26+.

Cấp quyền Camera và Notification khi hệ thống yêu cầu.

Bật FaceAccess trong phần Trợ năng / Accessibility của Android.

Mở FaceAccess và nhấn Bắt đầu theo dõi.

Chọn chế độ điều khiển phù hợp.

Nên thực hiện Hiệu chỉnh trước khi sử dụng lâu dài.

Tên và vị trí menu Accessibility có thể khác nhau giữa Samsung One UI, Meizu Flyme và các ROM Android khác.

Quyền và dữ liệu

FaceAccess sử dụng camera để phân tích khuôn mặt theo thời gian thực và Accessibility Service để thực hiện thao tác do người dùng chủ động kích hoạt. Dự án không yêu cầu tài khoản trực tuyến để sử dụng các chức năng cốt lõi.

Thông tin kỹ thuật về quyền và luồng dữ liệu nên được kiểm tra lại trước mỗi bản phát hành nếu bổ sung tính năng mới.

Kiểm thử

Tài liệu kiểm thử nằm trong:

development_history/testing/

Khuyến nghị trước mỗi release chạy:

.\gradlew clean
.\gradlew testDebugUnitTest
.\gradlew assembleDebug

Xem docs/TESTING.md và docs/COMPATIBILITY.md.

Báo lỗi và đóng góp

Bug tracker: GitHub Issues của repository.

Khi báo lỗi, vui lòng ghi rõ hãng máy, model, phiên bản Android/ROM, chế độ FaceAccess, thao tác tái hiện và log/ảnh nếu có.

Quy trình đóng góp: CONTRIBUTING.md.

Phát hành

Các bản phát hành phải có version rõ ràng theo dạng vMAJOR.MINOR.PATCH (ví dụ v1.0.0), release notes, APK và gói mã nguồn dạng mở phù hợp với thể lệ cuộc thi.

Lịch sử thay đổi: CHANGELOG.md.

Giấy phép mã nguồn mở

Mã nguồn do nhóm phát triển trong repository này được phát hành theo MIT License. Mục đích là cho phép cộng đồng xem, sử dụng, nghiên cứu, sửa đổi và phân phối lại dự án theo các điều khoản của giấy phép.

Xem toàn văn tại LICENSE. Các thành phần bên thứ ba vẫn giữ giấy phép và bản quyền của chủ sở hữu tương ứng, xem THIRD_PARTY_NOTICES.md.