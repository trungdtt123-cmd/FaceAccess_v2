<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Open Source Competition Checklist

Checklist này dùng để tự kiểm trước khi nộp FaceAccess.

Repository

GitHub repository ở trạng thái Public.

Commit history phản ánh quá trình phát triển thật.

GitHub web viewer truy cập được khi không đăng nhập.

License

Có toàn văn LICENSE MIT.

Có thông báo mục đích giấy phép trong README / NOTICE.

Kotlin/KTS production/test có SPDX header sau đợt rà soát 2026-09-15.

Có THIRD_PARTY_NOTICES.md.

Đã xác minh provenance của chính xác face_landmarker (1).task đang bundle bằng SHA-256 đối chiếu với endpoint chính thức của Google MediaPipe.

Đã ghi nguồn, ngày xác minh và checksum model trong THIRD_PARTY_NOTICES.md.

Release

Tạo GitHub Release v1.0.0 trước hạn nộp.

Có release notes.

Có APK.

Có source archive phù hợp yêu cầu thể lệ, ưu tiên .tar.gz thay vì chỉ có .zip.

Ghi SHA-256 cho APK và source archive trong release notes.

Đã ghi SHA-256 của model Face Landmarker.

Build from source

Có Gradle Wrapper.

README có lệnh build.

Có docs/BUILDING.md.

Có CI build/test.

Xác nhận CI xanh trên GitHub sau khi push.

Dependencies

Dependency version được pin trong Gradle/TOML.

Có bảng dependency/license.

Đã kiểm tra provenance/license của model asset trước release.

Documentation & communication

README.

CHANGELOG.

CONTRIBUTING.

Bug report template.

Feature request template.

Xác nhận GitHub Issues đang được bật và truy cập công khai.

Rà soát hiển thị Markdown trên GitHub sau lần cập nhật cuối.

Product / AI evidence

Có tài liệu kiến trúc AI hiện tại.

Có tài liệu calibration/testing.

Điền đủ model thiết bị + Android/ROM + commit SHA vào compatibility matrix.

Hoàn thành các test case còn PENDING.

Lưu bằng chứng test thực tế (ảnh/video/log/CSV).

Chuẩn bị ảnh/video showcase.

Nếu triển khai custom AI sau này: công bố dataset/features, training code, metrics và model artifact.