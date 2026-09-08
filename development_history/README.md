# Lịch Sử Phát Triển và Hiệu Chỉnh Hệ Thống
## Development & Calibration History

## Mục Đích

Thư mục này lưu trữ các thông tin và minh chứng liên quan đến quá trình
nghiên cứu, phát triển, thử nghiệm và hiệu chỉnh hệ thống FaceAccess v2.

FaceAccess v2 sử dụng MediaPipe Face Landmarker làm thành phần AI nền tảng
để phát hiện và phân tích các đặc trưng khuôn mặt.

Nhóm không huấn luyện lại mô hình MediaPipe Face Landmarker từ đầu.
Phần phát triển của nhóm tập trung vào việc xử lý dữ liệu đầu ra của
MediaPipe, xây dựng cơ chế nhận diện cử chỉ và phát triển hệ thống
hiệu chỉnh thích nghi cho từng người dùng.

## Quá Trình Phát Triển

Quá trình phát triển FaceAccess v2 gồm các giai đoạn chính:

### 1. Tích hợp nhận diện khuôn mặt

- Tích hợp MediaPipe Face Landmarker.
- Xử lý hình ảnh camera theo thời gian thực bằng CameraX.
- Thu nhận landmarks, blendshapes và facial transformation matrices.

### 2. Trích xuất dữ liệu khuôn mặt

Hệ thống trích xuất các thông số phục vụ nhận diện:

- Roll
- Yaw
- Pitch
- Độ nhắm mắt trái
- Độ nhắm mắt phải
- Độ mở miệng

### 3. Xây dựng nhận diện cử chỉ

Các cử chỉ được hệ thống xử lý gồm:

- Quay đầu trái/phải
- Nhìn lên/xuống
- Nghiêng đầu trái/phải
- Nhắm hai mắt
- Mở miệng
- Mở miệng hai lần

### 4. Kiểm thử và điều chỉnh

Hệ thống được kiểm thử trực tiếp trên thiết bị Android thật.

Các ngưỡng nhận diện được thử nghiệm và điều chỉnh nhằm giảm nhận diện sai
và tăng độ ổn định khi sử dụng.

Quy trình thực hiện:

Thử nghiệm → Đánh giá → Điều chỉnh → Kiểm thử lại

### 5. Phát triển cơ chế hiệu chỉnh thích nghi

FaceAccess v2 bổ sung cơ chế hiệu chỉnh theo từng người dùng.

Trong quá trình hiệu chỉnh, hệ thống thu thập mẫu của:

- Tư thế trung tính
- Quay đầu trái/phải
- Nhìn lên/xuống
- Nghiêng đầu trái/phải
- Nhắm mắt
- Mở miệng

Từ các mẫu thu thập được, hệ thống tính toán các giá trị đại diện và
xác định ngưỡng nhận diện phù hợp hơn với từng người dùng.

Cấu hình hiệu chỉnh được lưu lại để sử dụng cho các lần chạy tiếp theo.

## Minh Chứng

Các minh chứng phát triển có thể được lưu trong các thư mục:

- `calibration/`: dữ liệu hoặc hình ảnh liên quan đến quá trình hiệu chỉnh.
- `testing/`: kết quả kiểm thử trên thiết bị.
- `screenshots/`: ảnh giao diện và kết quả hoạt động của hệ thống.

Nếu chưa có minh chứng tương ứng, các thư mục này có thể được bổ sung
trong quá trình hoàn thiện sản phẩm.

## Theo Dõi Lịch Sử Phát Triển

Toàn bộ thay đổi mã nguồn của FaceAccess v2 được quản lý bằng Git.

Lịch sử commit của kho mã nguồn thể hiện:

- Các chức năng được bổ sung
- Các lỗi đã được sửa
- Các thay đổi trong thuật toán nhận diện
- Quá trình bổ sung cơ chế hiệu chỉnh thích nghi
- Các phiên bản của sản phẩm

Kho mã nguồn:

https://github.com/trungdtt123-cmd/FaceAccess_v2