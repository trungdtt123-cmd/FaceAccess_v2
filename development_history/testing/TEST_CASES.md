<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

# Kịch Bản Kiểm Thử FaceAccess v2
## Test Cases

Tài liệu này ghi nhận kết quả kiểm thử chức năng của FaceAccess v2.

> Trạng thái bên dưới được cập nhật theo xác nhận của nhóm rằng các kịch bản đã được kiểm thử thực tế và hoạt động đạt yêu cầu.

| ID | Hạng mục | Thao tác kiểm thử | Kết quả mong đợi | Trạng thái |
|---|---|---|---|---|
| TC-01 | Khởi động | Mở FaceAccess v2 | Ứng dụng mở bình thường, không crash | PASS |
| TC-02 | Camera | Cấp quyền camera và bắt đầu theo dõi | Camera trước hoạt động và hiển thị preview | PASS |
| TC-03 | MediaPipe | Đưa khuôn mặt vào camera | Hệ thống phát hiện được khuôn mặt | PASS |
| TC-04 | Không có khuôn mặt | Đưa khuôn mặt ra khỏi khung hình | Hệ thống phản hồi trạng thái không thấy khuôn mặt | PASS |
| TC-05 | Hiệu chỉnh | Bắt đầu quy trình hiệu chỉnh | Giao diện chuyển sang chế độ hiệu chỉnh và hiển thị bước hiện tại | PASS |
| TC-06 | Trung tính | Giữ đầu thẳng, mắt mở, miệng đóng | Hệ thống thu mẫu hợp lệ và tăng tiến độ | PASS |
| TC-07 | Quay trái | Thực hiện quay đầu sang trái | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-08 | Quay phải | Thực hiện quay đầu sang phải | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-09 | Nhìn lên | Ngẩng đầu lên | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-10 | Nhìn xuống | Cúi đầu xuống | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-11 | Nghiêng trái | Nghiêng đầu sang trái | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-12 | Nghiêng phải | Nghiêng đầu sang phải | Hệ thống nhận đúng tư thế và thu đủ mẫu | PASS |
| TC-13 | Nhắm mắt | Nhắm đồng thời hai mắt | Hệ thống nhận đúng trạng thái và thu đủ mẫu | PASS |
| TC-14 | Mở miệng | Mở miệng | Hệ thống nhận đúng trạng thái và thu đủ mẫu | PASS |
| TC-15 | Hoàn tất hiệu chỉnh | Hoàn thành toàn bộ 9 bước | Cấu hình cá nhân được tạo và lưu | PASS |
| TC-16 | Tải lại cấu hình | Đóng và mở lại ứng dụng sau hiệu chỉnh | Cấu hình hiệu chỉnh trước đó được tải lại | PASS |
| TC-17 | Accessibility | Nhấn bắt đầu theo dõi khi dịch vụ chưa bật | Ứng dụng mở phần cài đặt Trợ năng phù hợp hoặc trang dự phòng | PASS |
| TC-18 | Bật Accessibility | Bật FaceAccess v2 trong Trợ năng và quay lại ứng dụng | Ứng dụng nhận biết dịch vụ đã bật và tiếp tục theo dõi | PASS |
| TC-19 | Quay trái/phải | Thực hiện quay đầu trái và phải sau hiệu chỉnh | Hệ thống nhận diện đúng theo cấu hình cá nhân | PASS |
| TC-20 | Nhìn lên/xuống | Thực hiện ngẩng và cúi đầu | Hệ thống nhận diện đúng theo cấu hình cá nhân | PASS |
| TC-21 | Nghiêng đầu | Thực hiện nghiêng đầu trái và phải | Hệ thống nhận diện đúng theo cấu hình cá nhân | PASS |
| TC-22 | Nhắm mắt | Nhắm hai mắt theo thao tác đã cấu hình | Hệ thống kích hoạt hành động tương ứng | PASS |
| TC-23 | Mở miệng | Mở miệng theo thao tác đã cấu hình | Hệ thống kích hoạt hành động tương ứng | PASS |
| TC-24 | Mở miệng hai lần | Thực hiện hai lần theo đúng khoảng thời gian | Hệ thống nhận diện chuỗi cử chỉ tương ứng | PASS |
| TC-25 | Chuyển ứng dụng | Rời FaceAccess khi đang theo dõi | Camera và service được xử lý đúng theo thiết kế | PASS |
| TC-26 | Quay lại ứng dụng | Mở lại FaceAccess sau khi chuyển ứng dụng | Camera và trạng thái theo dõi được khôi phục đúng | PASS |
| TC-27 | Dừng theo dõi | Nhấn dừng theo dõi | Camera và tài nguyên MediaPipe được giải phóng phù hợp | PASS |
| TC-28 | Khởi động lại | Đóng và mở lại ứng dụng | Ứng dụng hoạt động bình thường, không crash | PASS |

## Tổng Kết

- Tổng số test case: **28**
- PASS: **28**
- FAIL: **0**
- Tỷ lệ đạt: **100%**

## Ghi Chú Minh Chứng

Ảnh và minh chứng kiểm thử thực tế được lưu tại:

```text
development_history/screenshots/