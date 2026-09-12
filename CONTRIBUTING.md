<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Contributing to FaceAccess v2

Cảm ơn bạn quan tâm tới FaceAccess v2.

Báo lỗi

Tạo GitHub Issue và cung cấp tối thiểu:

Hãng/model thiết bị.

Phiên bản Android và ROM/OEM skin.

Phiên bản FaceAccess hoặc commit SHA.

Chế độ đang sử dụng.

Các bước tái hiện lỗi.

Kết quả mong đợi và kết quả thực tế.

Logcat/ảnh/video nếu có thể.

Không đăng thông tin cá nhân nhạy cảm, số điện thoại thật hoặc dữ liệu khuôn mặt riêng tư trong issue công khai.

Đề xuất thay đổi mã nguồn

Tạo branch riêng từ main.

Giữ thay đổi nhỏ, có mục tiêu rõ ràng.

Không làm thay đổi hành vi đang ổn ở các chế độ khác nếu không cần thiết.

Bổ sung hoặc cập nhật test khi thay đổi detector/state machine.

Chạy kiểm thử trước khi mở pull request.

./gradlew clean
./gradlew testDebugUnitTest
./gradlew assembleDebug

Quy tắc tương thích

FaceAccess chạy trên nhiều OEM có cây Accessibility khác nhau. Khi sửa lỗi cho một hãng, ưu tiên fallback theo ngữ cảnh thay vì thay toàn bộ đường xử lý đang hoạt động trên hãng khác.

License header

Các tệp mã nguồn do dự án tạo mới phải có SPDX header:

SPDX-License-Identifier: MIT
Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

Sử dụng cú pháp comment phù hợp với loại tệp.