<!-- SPDX-License-Identifier: MIT -->
<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

# Kịch Bản Kiểm Thử FaceAccess v2
## Test Cases

Tài liệu này ghi nhận các kịch bản kiểm thử chức năng và hồi quy của FaceAccess v2.

> Chỉ đánh dấu `PASS` cho các trường hợp đã được nhóm kiểm thử thực tế và xác nhận hoạt động đạt yêu cầu. Các trường hợp chưa có đủ bằng chứng nên để `PENDING` cho tới khi kiểm thử hoàn tất.

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
| TC-27 | Dừng theo dõi | Nhấn dừng theo dõi | Camera và tài nguyên theo dõi được giải phóng phù hợp | PASS |
| TC-28 | Khởi động lại | Đóng và mở lại ứng dụng | Ứng dụng hoạt động bình thường, không crash | PASS |
| TC-29 | Cursor lifecycle | Ở chế độ Con trỏ, khóa con trỏ rồi dừng theo dõi và bật lại | Chỉ có một con trỏ; không còn overlay cũ | PASS |
| TC-30 | Cursor lock state | Khóa/mở khóa con trỏ sau khi dừng và bật lại theo dõi | Trạng thái khóa hiển thị đúng và con trỏ bị làm mờ khi khóa | PASS |
| TC-31 | Double-mouth priority | Mở miệng ngắn, đóng, mở lần hai và giữ trên 1 giây | Kích hoạt hành động mở miệng hai lần; không phát sinh BACK ngoài ý muốn | PASS |
| TC-32 | Meizu Dual SIM | Trong Hỗ trợ, chọn liên hệ, mở Dialer và hiển thị hộp chọn SIM | Hệ thống phát hiện đúng ngữ cảnh chọn SIM | PASS |
| TC-33 | YAW chọn SIM | Khi hộp chọn SIM đang mở, YAW trái/phải | Focus chuyển qua lại giữa các lựa chọn SIM | PASS |
| TC-34 | Xác nhận SIM | Sau khi chọn SIM bằng YAW, nhắm hai mắt có chủ đích | SIM đang focus được xác nhận và luồng gọi tiếp tục | PASS |
| TC-35 | Thiết bị không hỏi SIM | Thực hiện luồng gọi trên thiết bị không hiển thị hộp chọn SIM | Giữ nguyên luồng gọi thông thường, không bị chặn bởi logic Dual SIM | PASS |
| TC-36 | Meizu navigation | Ở chế độ Điều hướng, mở Cài đặt trên Meizu và thực hiện YAW | Focus di chuyển giữa các mục bằng fallback Accessibility phù hợp | PASS |
| TC-37 | Xác nhận mục Meizu | Sau khi YAW chọn mục trong Cài đặt trên Meizu, nhắm mắt | Mở đúng mục đang được focus | PASS |
| TC-38 | Samsung regression | Kiểm tra lại Điều hướng trên Samsung Galaxy Note20 Ultra sau các fix Meizu | Luồng Accessibility chuẩn vẫn hoạt động bình thường | PASS |
| TC-39 | Stop/Start navigation | Dừng theo dõi, bật lại và tiếp tục Điều hướng | YAW và thao tác xác nhận tiếp tục hoạt động | PASS |
| TC-40 | Intentional blink cross-mode | Nhắm hai mắt có chủ đích khoảng thời gian cấu hình ở Điều hướng, Media, Hỗ trợ và Con trỏ | Hành động tương ứng được kích hoạt đúng ở từng chế độ | PENDING |
| TC-41 | Blink after Stop/Start | Dừng theo dõi, bật lại rồi thực hiện nhắm mắt có chủ đích | Detector nhắm mắt hoạt động bình thường sau khi khởi động lại theo dõi | PENDING |

## Tổng Kết Hiện Tại

- Tổng số test case: **41**
- PASS đã ghi nhận: **39**
- PENDING cần xác nhận lại bằng kiểm thử thực tế: **2**
- FAIL đã ghi nhận: **0**

Hai test case đang để `PENDING` là các kiểm thử hồi quy nhắm mắt đa chế độ. Khi nhóm kiểm thử lại trực tiếp trên thiết bị và xác nhận đạt yêu cầu, đổi trạng thái sang `PASS` và cập nhật phần tổng kết.

## Ghi Chú Minh Chứng

Ảnh và minh chứng kiểm thử thực tế được lưu tại:

```text
development_history/screenshots/
```

Kết quả chi tiết theo thiết bị, phiên bản Android và commit nên được ghi trong:

```text
development_history/testing/test_results_template.csv
```

Thiết bị đã được sử dụng trong chu kỳ kiểm thử hiện tại:

- Samsung Galaxy Note20 Ultra.
- Meizu chạy Flyme OS; model chính xác chưa được ghi nhận.

Không ghi `PASS` cho model, phiên bản Android hoặc OEM UI chưa được kiểm thử thực tế.
