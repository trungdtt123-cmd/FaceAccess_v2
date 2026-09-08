<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

# Minh Chứng Hiệu Chỉnh
## Calibration Evidence

## 1. Mục Đích

Thư mục này lưu thông tin và minh chứng liên quan đến quá trình hiệu chỉnh thích nghi của FaceAccess v2.

FaceAccess v2 sử dụng MediaPipe Face Landmarker làm thành phần AI nền tảng để phát hiện và phân tích khuôn mặt.

Nhóm không huấn luyện lại mô hình MediaPipe Face Landmarker từ đầu.

Cơ chế hiệu chỉnh của FaceAccess v2 sử dụng dữ liệu đầu ra của MediaPipe để xác định đặc điểm cử chỉ của từng người dùng và thiết lập các ngưỡng nhận diện phù hợp hơn.

## 2. Dữ Liệu Được Sử Dụng

Hệ thống sử dụng các dữ liệu khuôn mặt:

- Roll
- Yaw
- Pitch
- Độ nhắm mắt trái
- Độ nhắm mắt phải
- Độ mở miệng

Các dữ liệu này được trích xuất từ kết quả của MediaPipe Face Landmarker.

## 3. Các Bước Hiệu Chỉnh

Quy trình hiệu chỉnh gồm 9 bước:

| STT | Bước | Mục Tiêu |
|---:|---|---|
| 1 | TRUNG_TINH | Xác định tư thế tự nhiên của người dùng |
| 2 | QUAY_TRAI | Xác định biên độ quay đầu sang trái |
| 3 | QUAY_PHAI | Xác định biên độ quay đầu sang phải |
| 4 | NHIN_LEN | Xác định biên độ nhìn lên |
| 5 | NHIN_XUONG | Xác định biên độ nhìn xuống |
| 6 | NGHIENG_TRAI | Xác định biên độ nghiêng đầu sang trái |
| 7 | NGHIENG_PHAI | Xác định biên độ nghiêng đầu sang phải |
| 8 | NHAM_HAI_MAT | Xác định mức độ nhắm hai mắt |
| 9 | MO_MIENG | Xác định mức độ mở miệng |

Mặc định hệ thống thu thập:

```text
20 mẫu hợp lệ cho mỗi bước