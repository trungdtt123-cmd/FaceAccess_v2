<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Changelog

Tất cả thay đổi đáng chú ý của FaceAccess v2 được ghi tại đây.

Dự án sử dụng định dạng phiên bản MAJOR.MINOR.PATCH và GitHub Release tương ứng cho mỗi bản phát hành chính thức.

[Unreleased]

Chưa có thay đổi nào sau v1.0.0.

[1.0.0] - 2026-09-15

Bản phát hành đầu tiên của FaceAccess v2 phục vụ cuộc thi Phát triển phần mềm mã nguồn mở tích hợp AI 2026.

Added

4 chế độ điều khiển: Điều hướng, Media, Hỗ trợ và Con trỏ.

Nhận diện cử chỉ đầu YAW/PITCH/ROLL.

Intentional blink.

Cử chỉ mở miệng và mở miệng hai lần.

Hiệu chỉnh thích nghi theo người dùng.

Điều khiển Android thông qua Accessibility Service.

Foreground tracking và quản lý vòng đời camera/service.

Cursor overlay và cơ chế khóa/mở khóa.

Support mode với luồng Dialer và lựa chọn SIM khi hệ thống yêu cầu.

Tương thích bổ sung cho Flyme/MEIZU trong điều hướng Accessibility.

Tài liệu kiến trúc, AI, build, testing, compatibility và lịch sử phát triển.

GitHub Actions CI.

Issue templates cho bug report và feature request.

Third-party notices và checklist nguồn mở.

Changed

Chuẩn hóa versionName từ 1.0 thành 1.0.0.

Hoàn thiện hồ sơ kiểm thử lịch sử và tài liệu compatibility.

Bổ sung thông tin thiết bị đã xác minh cho Samsung Galaxy Note20 Ultra và MEIZU Lucky 08.

Hoàn thiện provenance/checksum của model MediaPipe Face Landmarker.

Ổn định GitHub Actions cho Android SDK 37.

Verified

Local clean: PASS.

Local testDebugUnitTest: PASS.

Local assembleDebug: PASS.

Debug APK được tạo thành công tại app/build/outputs/apk/debug/app-debug.apk.

GitHub Actions Android CI: PASS.

Hồ sơ kiểm thử hiện ghi nhận 41/41 test case PASS.

Compatibility verified

Samsung Galaxy Note20 Ultra, model SM-N985F/DS, Android 13, One UI 5.1.

MEIZU Lucky 08, model M431Q, Android 14, Flyme 11.0.6.4G.