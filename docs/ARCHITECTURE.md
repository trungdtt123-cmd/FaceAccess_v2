<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Kiến trúc FaceAccess v2

Luồng xử lý chính

CameraX
↓
PhanTichKhungHinhKhuonMat / MediaPipe Face Landmarker
↓
TrichXuatDuLieuKhuonMat
↓
DuLieuKhuonMat
↓
BoChuanHoaDuLieuKhuonMat
↓
NhanDienHuongDau / NhanDienNghiengDau / NhanDienNhamHaiMat /
NhanDienMoMieng / NhanDienMoMiengHaiLan
↓
DieuPhoiCuChi
↓
BoDinhTuyenCheDo
↓
DichVuTruyCapFaceAccess
↓
Android Accessibility / overlay / media / support actions

Cấu hình và hiệu chỉnh

Cài đặt UI
↓
KhoCauHinhNhanDienCuChi
↓
SharedPreferences
↓
DichVuTheoDoiFaceAccess reload cấu hình
↓
Normalizer + detector runtime

Hiệu chỉnh thích nghi:

MediaPipe output
↓
BoThuThapMauHieuChinh
↓
BoHocNguongThichNghi
↓
profile cá nhân
↓
KhoCauHinhNhanDienCuChi
↓
BoChuanHoaDuLieuKhuonMat + detector

Nguyên tắc tương thích OEM

Ưu tiên API/Accessibility chuẩn trước.

Nếu OEM không expose cây Accessibility theo cách chuẩn, sử dụng fallback có điều kiện.

Không thay detector hoặc threshold toàn cục chỉ để sửa một lỗi UI riêng của một OEM.

Dừng/bật lại tracking phải reset state machine tạm thời nhưng giữ cấu hình người dùng đã lưu.