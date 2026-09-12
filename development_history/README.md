<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Lịch Sử Phát Triển và Hiệu Chỉnh Hệ Thống

Development & Calibration History

1. Mục Đích

Thư mục development_history/ lưu trữ tài liệu và minh chứng liên quan đến quá trình nghiên cứu, phát triển, thử nghiệm, hiệu chỉnh và cải tiến khả năng tương thích của FaceAccess v2.

FaceAccess v2 sử dụng MediaPipe Face Landmarker làm thành phần AI nền tảng để phát hiện và phân tích đặc trưng khuôn mặt theo thời gian thực. Nhóm không huấn luyện lại MediaPipe Face Landmarker từ đầu.

Phần phát triển của nhóm tập trung vào:

Tích hợp CameraX và MediaPipe vào ứng dụng Android.

Trích xuất dữ liệu khuôn mặt gồm roll, yaw, pitch, độ nhắm mắt trái, độ nhắm mắt phải và độ mở miệng.

Xây dựng bộ nhận diện cử chỉ theo thời gian và máy trạng thái.

Điều phối cử chỉ thành thao tác điều khiển thiết bị thông qua Accessibility Service.

Phát triển cơ chế hiệu chỉnh thích nghi theo từng người dùng.

Phát triển 4 chế độ: Điều hướng, Media, Hỗ trợ và Con trỏ.

Xử lý vòng đời theo dõi, con trỏ và foreground service khi dừng/bật lại theo dõi.

Hỗ trợ luồng gọi điện và giao diện chọn SIM trên thiết bị yêu cầu xác nhận SIM.

Bổ sung fallback Accessibility/Dialer cho một số giao diện OEM khác nhau.

Kiểm thử hồi quy trên thiết bị Android thực tế.

2. Cấu Trúc Thư Mục

development_history/
├── README.md
├── calibration/
│   ├── README.md
│   └── calibration_record_template.csv
├── testing/
│   ├── README.md
│   ├── TEST_CASES.md
│   └── test_results_template.csv
└── screenshots/
└── README.md

calibration/

Lưu mô tả, biểu mẫu và minh chứng của quy trình hiệu chỉnh thích nghi.

testing/

Lưu kế hoạch, kịch bản và kết quả kiểm thử chức năng, kiểm thử hồi quy và kiểm thử tương thích thiết bị.

screenshots/

Lưu ảnh giao diện, ảnh quá trình hiệu chỉnh, ảnh kiểm thử và ảnh kết quả hoạt động thực tế của ứng dụng.

3. Luồng Xử Lý Chính Của Hệ Thống

CameraX
↓
MediaPipe Face Landmarker
↓
Trích xuất dữ liệu khuôn mặt
↓
Chuẩn hóa dữ liệu theo tư thế trung tính
↓
Nhận diện cử chỉ
↓
Bộ điều phối theo chế độ
↓
Accessibility Service
↓
Thao tác điều khiển thiết bị

Trong một số tình huống đặc thù của thiết bị/OEM, tầng Accessibility sử dụng thêm cơ chế fallback để duy trì hành vi tương thích mà không thay đổi bộ nhận diện cử chỉ gốc.

4. Các Cử Chỉ Được Xử Lý

Hệ thống hiện hỗ trợ các nhóm cử chỉ chính:

Quay đầu sang trái.

Quay đầu sang phải.

Ngẩng đầu lên.

Cúi đầu xuống.

Nghiêng đầu sang trái.

Nghiêng đầu sang phải.

Nhắm hai mắt có chủ đích.

Mở miệng.

Mở miệng hai lần.

Ý nghĩa của cùng một cử chỉ có thể thay đổi theo chế độ đang hoạt động. Ví dụ YAW được dùng để điều hướng mục, chọn liên hệ hoặc chuyển lựa chọn SIM tùy ngữ cảnh.

5. Cơ Chế Hiệu Chỉnh Thích Nghi

Cơ chế hiệu chỉnh được triển khai trong:

app/src/main/java/com/example/faceaccess/v2/ai/hieuchinh/

Các thành phần chính:

BuocHieuChinh.kt: định nghĩa các bước hiệu chỉnh.

BoThuThapMauHieuChinh.kt: thu thập mẫu cho từng bước.

BoDieuKhienHieuChinh.kt: điều khiển trình tự, kiểm tra tư thế và tiến độ.

BoHocNguongThichNghi.kt: tính cấu hình cá nhân từ dữ liệu hiệu chỉnh.

BoChuanHoaDuLieuKhuonMat.kt: bù sai lệch tư thế trung tính.

Mặc định hệ thống thu 20 mẫu hợp lệ cho mỗi bước hiệu chỉnh.

Các bước gồm:

Tư thế trung tính.

Quay trái.

Quay phải.

Nhìn lên.

Nhìn xuống.

Nghiêng trái.

Nghiêng phải.

Nhắm hai mắt.

Mở miệng.

Sau khi đủ dữ liệu, hệ thống tính các giá trị đại diện và xác định ngưỡng nhận diện phù hợp hơn với người dùng. Cấu hình cá nhân sau hiệu chỉnh được lưu lại để sử dụng cho các lần chạy tiếp theo.

Hiệu chỉnh không phải quá trình train lại MediaPipe. Đây là cơ chế cá nhân hóa các ngưỡng và mốc trung tính dựa trên dữ liệu người dùng thực tế.

6. Các Mốc Cải Tiến Quan Trọng

Quá trình phát triển gần đây tập trung vào các nhóm cải tiến sau:

Ổn định nhận diện YAW/PITCH và trạng thái rearm.

Cải thiện nhắm hai mắt có chủ đích quanh ngưỡng thời gian cấu hình.

Tách xung đột giữa mở miệng giữ và mở miệng hai lần.

Siết vòng đời con trỏ để tránh overlay cũ còn tồn tại sau khi dừng/bật lại theo dõi.

Cải thiện tương thích Accessibility trên Flyme/Meizu mà vẫn giữ đường xử lý chuẩn trên Samsung.

Cải thiện mở Dialer và luồng gọi điện trên giao diện OEM khác nhau.

Hỗ trợ hộp thoại chọn SIM bằng YAW trái/phải và nhắm mắt xác nhận.

Duy trì kiểm thử hồi quy để tránh làm hỏng các chức năng đã ổn định.

Chi tiết theo phiên bản được ghi trong:

CHANGELOG.md

7. Thiết Bị Kiểm Thử Thực Tế

Các thiết bị đã được sử dụng trong quá trình kiểm thử hiện tại gồm:

Samsung Galaxy Note20 Ultra.

Meizu chạy Flyme OS; model chính xác và phiên bản Android chưa được ghi nhận trong chu kỳ kiểm thử hiện tại.

Chi tiết tương thích được duy trì tại:

docs/COMPATIBILITY.md

Không suy rộng kết quả của một model thành toàn bộ thiết bị của cùng hãng nếu chưa kiểm thử.

8. Theo Dõi Lịch Sử Phát Triển

Toàn bộ thay đổi mã nguồn được quản lý bằng Git và GitHub.

Lịch sử commit được sử dụng để thể hiện:

Các chức năng mới.

Các lỗi đã sửa.

Các thay đổi trong thuật toán nhận diện.

Việc bổ sung hệ thống hiệu chỉnh thích nghi.

Các thay đổi tương thích thiết bị/OEM.

Các thay đổi về tài liệu và giấy phép.

Các mốc phát hành của sản phẩm.

Kho mã nguồn:

https://github.com/trungdtt123-cmd/FaceAccess_v2

9. Nguyên Tắc Lưu Minh Chứng

Chỉ lưu các kết quả đã thực hiện thật. Không điền số liệu, thiết bị, kết quả hoặc ảnh minh chứng nếu chưa kiểm thử.

Mỗi minh chứng nên có:

Ngày thực hiện.

Phiên bản hoặc commit được kiểm thử.

Thiết bị sử dụng.

Phiên bản Android/OEM UI nếu biết.

Mục tiêu kiểm thử.

Kết quả thực tế.

Ảnh hoặc log liên quan nếu có.

Khi bổ sung ảnh, nên dùng tên dễ truy vết, ví dụ:

01_main_screen.png
02_calibration.png
03_cursor_mode.png
04_samsung_note20_ultra.png
05_meizu_navigation.png
06_meizu_dual_sim.png