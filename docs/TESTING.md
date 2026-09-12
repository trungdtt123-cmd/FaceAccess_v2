<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Testing

Automated checks

./gradlew testDebugUnitTest
./gradlew assembleDebug

GitHub Actions cũng chạy hai lệnh này cho push/pull request vào main.

Manual regression checklist

Trước mỗi release cần kiểm tra tối thiểu:

Bắt đầu theo dõi → Dừng → Bật lại.

Điều hướng: Yaw trái/phải, Pitch lên/xuống, blink xác nhận.

Media: các gesture tương ứng.

Hỗ trợ: chọn liên hệ, mở Dialer, gọi, Dual SIM nếu có.

Con trỏ: hiển thị duy nhất một cursor, khóa/mở khóa, click bằng blink.

Mở miệng giữ và mở miệng hai lần không xung đột.

Hiệu chỉnh mới, hủy hiệu chỉnh và hiệu chỉnh lại.

Đóng/mở Activity trong khi foreground tracking đang hoạt động.

Chi tiết test case lịch sử xem development_history/testing/.