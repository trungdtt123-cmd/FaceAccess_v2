# Nhật Ký Phát Triển và Huấn Luyện Mô Hình (Training & Development History)

Nhóm phát triển:
* **Hoàng Thị Kiều Anh**
* **Phạm Văn Dượng**
* **Đặng Quốc Trung**

## Quy trình thực hiện thực tế của nhóm:
Do quá trình nghiên cứu và huấn luyện mô hình AI (Face Landmarks) được nhóm thực hiện trực tiếp thông qua các công cụ trực tuyến và môi trường thử nghiệm nhanh (như Google Colab / Kaggle) theo hình thức thử nghiệm - sửa lỗi liên tục (trial and error), nhóm tập trung chủ yếu vào việc tối ưu hóa code inference và tích hợp hoàn chỉnh vào ứng dụng Android thực tế.

Do đó, thư mục này tập trung lưu trữ các cấu hình, thông số kiến trúc mô hình đã qua kiểm thử và mã nguồn tích hợp cốt lõi nằm trọn vẹn trong thư mục `app/` của dự án. Quá trình kiểm chứng năng lực thực hiện của các thành viên có thể được đánh giá trực tiếp qua mã nguồn source code sạch sẽ, logic phân chia module rõ ràng (`camera`, `cuchi`, `dieuphoi`, `khuonmat`) và khả năng chạy thực tế của ứng dụng trên thiết bị.