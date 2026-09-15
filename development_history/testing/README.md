<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Kiểm Thử Hệ Thống

System Testing

1. Mục Đích

Thư mục này lưu kế hoạch, test case và hồ sơ kiểm thử của FaceAccess v2 trên thiết bị Android thực tế.

Mục tiêu là giúp truy vết:

chức năng nào đã được kiểm thử;

thiết bị nào đã được sử dụng;

commit hoặc mốc phát triển liên quan;

kết quả PASS/FAIL;

phần dữ liệu nào là hồ sơ lịch sử và phần nào là dữ liệu của các phiên test mới.

2. Phạm Vi Kiểm Thử

Các thành phần chính:

khởi động ứng dụng;

CameraX;

MediaPipe Face Landmarker;

phát hiện khuôn mặt;

hiệu chỉnh cá nhân;

nhận diện YAW, PITCH và ROLL;

intentional blink;

mở miệng và mở miệng hai lần;

Accessibility Service;

4 chế độ Điều hướng, Media, Hỗ trợ và Con trỏ;

foreground tracking;

vòng đời Stop/Start;

cursor overlay;

Dialer và Dual SIM;

tương thích Samsung và Flyme/MEIZU.

3. Các Tệp Trong Thư Mục

TEST_CASES.md

Danh sách 41 test case chức năng và hồi quy cùng trạng thái tổng quan.

Trạng thái hiện tại:

Tổng: 41

PASS: 41

FAIL: 0

PENDING: 0

test_results_history.csv

Hồ sơ lịch sử của các test đã được thực hiện trong quá trình phát triển.

Một số ngày được tái dựng từ mốc commit liên quan vì biểu mẫu kiểm thử được chuẩn hóa sau khi các lần test ban đầu đã diễn ra. Các bản ghi như vậy được đánh dấu:

RECONSTRUCTED_FROM_COMMIT_HISTORY

Không tự điền latency, số lần lặp, ảnh/video hoặc raw log nếu các dữ liệu đó không được ghi lại tại thời điểm test.

test_results_template.csv

Biểu mẫu dùng cho các phiên kiểm thử mới.

Khi test mới, nên ghi ngay:

ngày và giờ;

version hoặc commit SHA;

tester ID;

thiết bị;

model;

Android;

OEM UI / ROM;

mode;

test-case ID;

thao tác;

kết quả mong đợi;

kết quả thực tế;

PASS / FAIL;

latency nếu có đo;

ảnh/video/log nếu có;

ghi chú.

4. Quy Trình Kiểm Thử

Mỗi phiên test mới nên đi theo quy trình:

Chuẩn bị thiết bị
↓
Ghi version / commit
↓
Thực hiện test case
↓
Quan sát kết quả
↓
So sánh với kết quả mong đợi
↓
PASS / FAIL
↓
Ghi kết quả vào CSV
↓
Lưu ảnh / video / log nếu cần

5. Thiết Bị Đã Ghi Nhận

Samsung

Samsung Galaxy Note20 Ultra.

Android version / One UI / model code: chỉ bổ sung khi có thông tin đã xác minh.

MEIZU

Tên thiết bị: MEIZU Lucky 08.

Model: M431Q.

Android: 14.

OEM UI / ROM: Flyme 11.0.6.4G.

6. Nguyên Tắc Dùng Dữ Liệu

test_results_history.csv phù hợp để:

chứng minh quá trình phát triển;

ghi nhận regression testing;

ghi nhận compatibility testing;

phục vụ hồ sơ release và cuộc thi.

Nếu sử dụng cho bài báo khoa học và cần Accuracy, Precision, Recall, F1 hoặc latency, nên tạo một đợt thực nghiệm mới có protocol định lượng rõ ràng. Không tính các chỉ số thống kê từ những trường lịch sử chưa ghi số lần lặp hoặc raw measurement.