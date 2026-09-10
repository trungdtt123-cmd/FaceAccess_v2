# FaceAccess v2 - Ứng Dụng Điều Khiển Bằng Cử Chỉ Khuôn Mặt

FaceAccess v2 là ứng dụng Android hỗ trợ người dùng điều khiển thiết bị thông qua các cử chỉ khuôn mặt như nhắm mắt, mở miệng, quay đầu và nghiêng đầu.

Hệ thống sử dụng MediaPipe Face Landmarker để phát hiện và phân tích các đặc trưng khuôn mặt, kết hợp cơ chế hiệu chỉnh thích nghi nhằm cá nhân hóa ngưỡng nhận diện cho từng người dùng.

Ứng dụng hướng tới hỗ trợ khả năng tương tác với thiết bị Android mà không cần thao tác trực tiếp bằng tay.

## Thành Viên Nhóm Phát Triển

Dự án được thực hiện bởi nhóm sinh viên:

1. **Hoàng Thị Kiều Anh**
2. **Phạm Văn Dượng**
3. **Đặng Quốc Trung**

## Chức Năng Chính

- Nhận diện khuôn mặt theo thời gian thực bằng MediaPipe Face Landmarker.
- Nhận diện hướng quay đầu sang trái và sang phải.
- Nhận diện chuyển động nhìn lên và nhìn xuống.
- Nhận diện nghiêng đầu sang trái và sang phải.
- Nhận diện nhắm hai mắt.
- Nhận diện mở miệng và mở miệng hai lần.
- Điều khiển con trỏ và các thao tác trên thiết bị Android.
- Tích hợp Android Accessibility Service để thực hiện các thao tác điều khiển.
- Hỗ trợ nhiều chế độ điều khiển khác nhau.
- Hiệu chỉnh cử chỉ theo từng người dùng.
- Tự động thu thập mẫu và tính toán ngưỡng nhận diện thích nghi.
- Lưu cấu hình hiệu chỉnh để sử dụng lại ở các lần chạy sau.

## Công Nghệ Sử Dụng

- Kotlin
- Android SDK
- AndroidX
- CameraX
- MediaPipe Tasks Vision
- MediaPipe Face Landmarker
- Android Accessibility Service
- Material Components
- Gradle

## Cấu Trúc Dự Án

- **`app/`**: Mã nguồn chính của ứng dụng Android.
- **`app/src/main/java/`**: Các lớp Kotlin của hệ thống.
- **`app/src/main/res/`**: Giao diện và tài nguyên Android.
- **`app/src/main/assets/`**: Các tài nguyên phục vụ nhận diện khuôn mặt.
- **`training_history/`**: Lịch sử huấn luyện, biểu đồ và các minh chứng quá trình phát triển mô hình của nhóm.
- **`LICENSE`**: Toàn văn giấy phép mã nguồn mở MIT.
- **`gradle/`**: Cấu hình và dependency của hệ thống Gradle.

## Yêu Cầu Môi Trường

Để biên dịch dự án từ mã nguồn, cần:

- Android Studio
- JDK phù hợp với phiên bản Android Gradle Plugin của dự án
- Android SDK 37
- Thiết bị Android từ API 26 trở lên
- Kết nối Internet trong lần build đầu tiên để tải các dependency

## Biên Dịch Từ Mã Nguồn

Clone kho mã nguồn:

```bash
git clone https://github.com/trungdtt123-cmd/FaceAccess_v2.git