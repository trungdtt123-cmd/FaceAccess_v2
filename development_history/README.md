<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Lịch Sử Phát Triển và Hiệu Chỉnh Hệ Thống

Development & Calibration History

1. Mục Đích

Thư mục development_history/ lưu tài liệu và minh chứng liên quan đến quá trình nghiên cứu, phát triển, thử nghiệm, hiệu chỉnh và cải tiến khả năng tương thích của FaceAccess v2.

FaceAccess v2 sử dụng MediaPipe Face Landmarker làm thành phần AI nền tảng để phân tích khuôn mặt theo thời gian thực. Nhóm không huấn luyện lại MediaPipe Face Landmarker từ đầu.

Phần phát triển của nhóm tập trung vào:

tích hợp CameraX và MediaPipe;

trích xuất roll, yaw, pitch, độ nhắm mắt và độ mở miệng;

nhận diện cử chỉ theo thời gian và máy trạng thái;

điều phối cử chỉ theo từng mode;

điều khiển Android bằng Accessibility Service;

hiệu chỉnh thích nghi theo người dùng;

4 chế độ Điều hướng, Media, Hỗ trợ và Con trỏ;

vòng đời tracking/service/cursor;

Dialer và Dual SIM;

fallback tương thích theo OEM;

kiểm thử hồi quy trên thiết bị thật.

2. Cấu Trúc Thư Mục

development_history/
├── README.md
├── calibration/
│   ├── README.md
│   └── calibration_record_template.csv
├── testing/
│   ├── README.md
│   ├── TEST_CASES.md
│   ├── test_results_history.csv
│   └── test_results_template.csv
└── screenshots/
└── README.md

calibration/

Lưu mô tả và biểu mẫu của quá trình hiệu chỉnh thích nghi.

testing/

Lưu test case, hồ sơ test lịch sử và biểu mẫu cho các phiên test mới.

screenshots/

Lưu ảnh giao diện, quá trình hiệu chỉnh, kiểm thử và minh chứng hoạt động của ứng dụng.

3. Luồng Xử Lý Chính

CameraX
↓
MediaPipe Face Landmarker
↓
Trích xuất dữ liệu khuôn mặt
↓
Chuẩn hóa theo tư thế trung tính
↓
Nhận diện cử chỉ
↓
Bộ điều phối theo chế độ
↓
Accessibility Service
↓
Thao tác điều khiển thiết bị

Trong một số giao diện OEM, tầng Accessibility sử dụng fallback để duy trì khả năng tương thích mà không thay đổi bộ nhận diện cử chỉ gốc.

4. Các Cử Chỉ Chính

Hệ thống hiện xử lý:

quay đầu trái;

quay đầu phải;

ngẩng đầu;

cúi đầu;

nghiêng đầu trái;

nghiêng đầu phải;

nhắm hai mắt có chủ đích;

mở miệng;

mở miệng hai lần.

Ý nghĩa của cùng một cử chỉ có thể thay đổi theo chế độ hoặc ngữ cảnh đang hoạt động.

5. Cơ Chế Hiệu Chỉnh Thích Nghi

Các thành phần chính nằm tại:

app/src/main/java/com/example/faceaccess/v2/ai/hieuchinh/

Gồm:

BuocHieuChinh.kt

BoThuThapMauHieuChinh.kt

BoDieuKhienHieuChinh.kt

BoHocNguongThichNghi.kt

BoChuanHoaDuLieuKhuonMat.kt

Hệ thống thu mẫu hợp lệ theo từng bước, xác định mốc trung tính và các ngưỡng cá nhân hóa phù hợp hơn với người dùng.

Hiệu chỉnh không phải quá trình train lại MediaPipe. Đây là bước cá nhân hóa tham số nhận diện dựa trên dữ liệu người dùng.

6. Các Mốc Cải Tiến Quan Trọng

Các nhóm cải tiến đã được ghi nhận gồm:

ổn định YAW/PITCH và rearm;

cải thiện intentional blink;

xử lý xung đột mở miệng giữ và mở miệng hai lần;

siết vòng đời cursor overlay;

tăng tương thích Accessibility trên Flyme/MEIZU;

giữ regression path cho Samsung;

tăng tương thích Dialer;

hỗ trợ chọn SIM bằng YAW và xác nhận bằng blink;

tăng tính ổn định sau Stop/Start;

bổ sung tài liệu nguồn mở, CI và hồ sơ kiểm thử.

Chi tiết theo phiên bản được duy trì tại:

CHANGELOG.md

7. Thiết Bị Kiểm Thử Đã Ghi Nhận

Samsung Galaxy Note20 Ultra

Model: SM-N985F/DS

Android: 13

One UI: 5.1

Đã được sử dụng cho kiểm thử chức năng và regression.

Thông tin trên được xác minh trực tiếp từ thiết bị. Serial number, IMEI và các mã định danh thiết bị duy nhất không được đưa vào repository công khai.

MEIZU Lucky 08

Model: M431Q

Android: 14

Flyme: 11.0.6.4G

Đã được sử dụng cho các kiểm thử compatibility, navigation, Dialer và Dual SIM.

Chi tiết được duy trì tại:

docs/COMPATIBILITY.md

8. Hồ Sơ Kiểm Thử

Danh sách test case:

development_history/testing/TEST_CASES.md

Hồ sơ các test đã thực hiện trong quá trình phát triển:

development_history/testing/test_results_history.csv

Biểu mẫu cho các phiên test mới:

development_history/testing/test_results_template.csv

Hiện trạng tổng quan:

41 test case;

41 PASS;

0 FAIL;

0 PENDING.

Một số ngày trong test_results_history.csv được tái dựng từ lịch sử commit vì tài liệu kiểm thử được chuẩn hóa sau quá trình phát triển. Những dòng này được đánh dấu rõ bằng RECONSTRUCTED_FROM_COMMIT_HISTORY.

Không suy đoán latency, số lần lặp hoặc raw evidence khi các thông tin đó không được ghi nhận tại thời điểm test.

9. Theo Dõi Lịch Sử Phát Triển

Thay đổi mã nguồn được quản lý bằng Git và GitHub.

Lịch sử commit giúp truy vết:

chức năng mới;

bug fix;

thay đổi thuật toán;

hiệu chỉnh;

tương thích thiết bị/OEM;

tài liệu và giấy phép;

các mốc phát hành.

Kho mã nguồn:

https://github.com/trungdtt123-cmd/FaceAccess_v2

10. Nguyên Tắc Lưu Minh Chứng

Với các phiên test mới, nên ghi ngay:

ngày/giờ;

version hoặc commit;

thiết bị;

Android/OEM UI;

test case;

expected result;

actual result;

PASS/FAIL;

latency nếu có đo;

ảnh/video/log nếu cần.

Không đưa dữ liệu riêng tư, số điện thoại, danh bạ hoặc dữ liệu khuôn mặt thô vào repository công khai.