<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Testing

Tài liệu này mô tả kiểm tra tối thiểu trước khi phát hành FaceAccess v2 và cách tổ chức bằng chứng kiểm thử.

Automated Checks

Chạy tại thư mục gốc của project:

./gradlew testDebugUnitTest

./gradlew assembleDebug

GitHub Actions cũng chạy hai lệnh trên cho push hoặc pull request vào main.

Kết quả hợp lệ khi Gradle kết thúc với:

BUILD SUCCESSFUL

Các warning deprecation không đồng nghĩa với build failure. Chỉ xem một bước là thất bại khi Gradle/GitHub Actions báo lỗi hoặc trả về trạng thái failed.

Manual Regression Checklist

Trước release cần kiểm tra tối thiểu:

Bắt đầu theo dõi → Dừng → Bật lại.

Điều hướng: YAW trái/phải, PITCH lên/xuống, blink xác nhận.

Media: các gesture tương ứng.

Hỗ trợ: chọn liên hệ, mở Dialer, gọi và Dual SIM nếu có.

Con trỏ: chỉ có một cursor, khóa/mở khóa, click bằng blink, không còn overlay cũ sau Stop/Start.

Mở miệng giữ và mở miệng hai lần không xung đột.

Hiệu chỉnh mới, hủy hiệu chỉnh và hiệu chỉnh lại.

Đóng/mở Activity trong khi foreground tracking đang hoạt động.

Intentional blink hoạt động ở cả bốn chế độ.

Intentional blink hoạt động sau Stop/Start.

Danh sách đầy đủ 41 test case:

development_history/testing/TEST_CASES.md

Trạng thái hồ sơ hiện tại:

41 PASS

0 FAIL

0 PENDING

Test Evidence

FaceAccess sử dụng hai tệp kết quả:

Historical results

development_history/testing/test_results_history.csv

Tệp này lưu các test đã được thực hiện trong quá trình phát triển.

Một số trường reconstructed_test_date được tái dựng từ lịch sử commit vì biểu mẫu kiểm thử được chuẩn hóa sau các lần test ban đầu. Các hàng đó được đánh dấu:

RECONSTRUCTED_FROM_COMMIT_HISTORY

Trạng thái PASS thể hiện xác nhận lịch sử của người phát triển/nhóm đối với test case tương ứng.

Không suy đoán các số liệu chưa được đo như:

repetition count;

latency;

screenshot/video;

raw log.

Template for future sessions

development_history/testing/test_results_template.csv

Tệp này được dùng cho các phiên test mới.

Tối thiểu nên ghi:

ngày và giờ;

version hoặc commit SHA;

tester ID;

thiết bị và model;

Android;

OEM UI / ROM;

mode;

test-case ID;

thao tác/cử chỉ;

expected result;

actual result;

PASS/FAIL;

latency nếu có đo;

đường dẫn ảnh/video/log;

ghi chú.

Ảnh/video bổ sung có thể lưu tại:

development_history/screenshots/

Không commit log chứa dữ liệu riêng tư, số điện thoại, danh bạ hoặc dữ liệu khuôn mặt thô.

Test Devices Recorded

Samsung Galaxy Note20 Ultra

Model: SM-N985F/DS

Android: 13

One UI: 5.1

Được sử dụng trong kiểm thử chức năng và regression.

Thông tin thiết bị được xác minh trực tiếp từ màn hình thông tin điện thoại. Serial number và IMEI không được lưu trong hồ sơ công khai.

MEIZU Lucky 08

Model: M431Q

Android: 14

Flyme: 11.0.6.4G

Được sử dụng cho các kiểm thử compatibility, navigation, Dialer và Dual SIM.

Release Gate

Chỉ tạo release chính thức khi:

local testDebugUnitTest thành công;

local assembleDebug thành công;

GitHub Actions trên commit phát hành ở trạng thái xanh;

không còn test case bắt buộc ở trạng thái FAIL;

compatibility matrix được cập nhật;

release notes phản ánh đúng trạng thái kiểm thử;

model provenance/license đã được xác minh.

Dùng Dữ Liệu Cho Bài Báo

test_results_history.csv là hồ sơ lịch sử phát triển và regression testing.

Nếu cần báo cáo Accuracy, Precision, Recall, F1, latency hoặc so sánh định lượng trong bài báo, nên thực hiện một đợt thực nghiệm mới với:

protocol cố định;

số người tham gia rõ ràng;

số lần lặp rõ ràng;

thiết bị và điều kiện test được kiểm soát;

raw measurement được ghi ngay tại thời điểm test.

Không suy ra các chỉ số thống kê từ các ô lịch sử bị bỏ trống.