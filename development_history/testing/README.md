<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

# Kiểm Thử Hệ Thống
## System Testing

## 1. Mục Đích

Thư mục này lưu kế hoạch và kết quả kiểm thử FaceAccess v2 trên thiết bị Android thực tế.

## 2. Phạm Vi Kiểm Thử

Các thành phần chính cần được kiểm thử:

- Khởi động ứng dụng.
- CameraX.
- MediaPipe Face Landmarker.
- Nhận diện khuôn mặt.
- Hiệu chỉnh cá nhân.
- Nhận diện hướng đầu.
- Nhận diện nghiêng đầu.
- Nhận diện nhắm mắt.
- Nhận diện mở miệng.
- Accessibility Service.
- Điều khiển thiết bị.
- Foreground Service.
- Chuyển ứng dụng.
- Giải phóng camera.
- Khôi phục hoạt động.

## 3. Quy Trình

Mỗi test case thực hiện theo quy trình:

```text
Chuẩn bị
↓
Thực hiện thao tác
↓
Quan sát kết quả
↓
So sánh với kết quả mong đợi
↓
PASS / FAIL
↓
Lưu ảnh hoặc Logcat nếu cần