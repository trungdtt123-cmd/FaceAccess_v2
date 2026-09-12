<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

AI Architecture

Thành phần AI hiện tại

FaceAccess sử dụng MediaPipe Face Landmarker ở tầng perception. Đây là mô hình ML đã được huấn luyện sẵn và được chạy ở chế độ inference trên thiết bị.

Đầu ra được FaceAccess sử dụng gồm:

Face landmarks.

Face blendshape scores.

Facial transformation matrices.

Từ đó hệ thống trích xuất các đặc trưng phục vụ điều khiển như Yaw, Pitch, Roll, trạng thái mắt và độ mở miệng.

Phần do nhóm phát triển

Nhóm không train lại trọng số MediaPipe. Các thành phần do nhóm xây dựng gồm:

Pipeline CameraX → MediaPipe → dữ liệu đặc trưng.

Trích xuất và chuẩn hóa Yaw/Pitch/Roll.

Hiệu chỉnh thích nghi theo người dùng.

Bộ nhận diện cử chỉ theo thời gian và state machine.

Cơ chế chống nhiễu, re-arm và lifecycle reset.

Điều phối cử chỉ theo 4 chế độ.

Accessibility execution và OEM compatibility fallback.

Hiệu chỉnh không phải training neural network

Hiệu chỉnh thu mẫu từ người dùng để xác định tư thế trung tính và ngưỡng phù hợp. Nó không sử dụng backpropagation, optimizer hoặc cập nhật weights của MediaPipe.

Vì vậy cách mô tả chính xác là:

AI perception bằng pretrained MediaPipe + adaptive personalization + deterministic gesture engine.

Hướng phát triển AI do nhóm tự train

Một hướng mở rộng phù hợp là custom gesture classifier dùng feature vector từ MediaPipe, ví dụ:

yaw, pitch, roll,
blinkLeft, blinkRight, jawOpen,
deltaYaw, deltaPitch, deltaRoll,
temporal statistics

Các nhãn có thể gồm NEUTRAL, YAW_LEFT, YAW_RIGHT, PITCH_UP, PITCH_DOWN, TILT_LEFT, TILT_RIGHT, BLINK, MOUTH_OPEN.

Mô hình nên được tích hợp theo cơ chế hybrid: AI tạo candidate khi confidence đủ cao, còn state machine/rule-based hiện tại đóng vai trò kiểm tra và fallback nhằm bảo đảm tính ổn định của ứng dụng trợ năng.