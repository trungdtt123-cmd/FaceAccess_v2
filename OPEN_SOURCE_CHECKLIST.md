<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Open Source Competition Checklist

Checklist tự kiểm trước khi nộp FaceAccess v2.

Repository

GitHub repository ở trạng thái Public.

Commit history phản ánh quá trình phát triển.

GitHub web viewer truy cập được.

GitHub Issues được bật.

License

Có toàn văn LICENSE MIT.

Có thông báo giấy phép trong README / NOTICE.

Các tệp Kotlin/KTS đã được rà soát SPDX header.

Có THIRD_PARTY_NOTICES.md.

Đã xác minh provenance của model face_landmarker (1).task.

Đã đối chiếu SHA-256 model với artifact chính thức của Google MediaPipe.

Đã ghi thông tin model/license/checksum trong hồ sơ third-party.

Versioning

versionCode = 1.

versionName = "1.0.0".

Tạo Git tag v1.0.0.

Tạo GitHub Release v1.0.0.

Release artifacts

Có APK phát hành đính kèm GitHub Release.

Có source archive .tar.gz.

Có release notes.

Ghi SHA-256 của APK.

Ghi SHA-256 của source archive.

Đã ghi SHA-256 của model Face Landmarker.

Build from source

Có Gradle Wrapper.

README có hướng dẫn build.

Có docs/BUILDING.md.

Local clean thành công.

Local testDebugUnitTest thành công.

Local assembleDebug thành công.

Debug APK được tạo thành công.

Có GitHub Actions build/test.

GitHub Actions Android CI đã chạy xanh sau khi sửa cấu hình Android SDK.

Dependencies

Dependency version được pin trong Gradle/TOML.

Có bảng dependency/license.

Đã kiểm tra provenance/license model asset.

Không sửa model MediaPipe bundle trong project.

Documentation & communication

README.

CHANGELOG.

CONTRIBUTING.

NOTICE.

THIRD_PARTY_NOTICES.

Bug report template.

Feature request template.

docs/ARCHITECTURE.md.

docs/AI_ARCHITECTURE.md.

docs/BUILDING.md.

docs/TESTING.md.

docs/COMPATIBILITY.md.

Testing & compatibility evidence

Có development_history/testing/TEST_CASES.md.

Có test_results_history.csv.

Có test_results_template.csv cho các phiên test mới.

Hồ sơ hiện ghi nhận 41/41 test case PASS.

Samsung Galaxy Note20 Ultra đã có model, Android và One UI được xác minh.

MEIZU Lucky 08 đã có model, Android và Flyme được xác minh.

Bổ sung thêm ảnh/video showcase trước khi nộp nếu có thể.

Product / AI evidence

Có tài liệu kiến trúc AI hiện tại.

Có tài liệu calibration/testing.

MediaPipe Face Landmarker được mô tả đúng là pretrained ML inference, không ghi sai thành model do nhóm train.

Có tài liệu mô tả gesture logic và adaptive calibration.

Nếu triển khai custom AI trong tương lai: công bố dataset/features, training code, metrics và model artifact.

Final release gate

Chỉ tạo release chính thức khi:

Local build/test xanh.

GitHub Actions xanh.

Compatibility/testing documentation đã đồng bộ.

License/model provenance đã khóa.

Commit thay đổi versionName = "1.0.0" và tài liệu release.

Push commit release-preparation lên main.

Xác nhận CI xanh trên commit release-preparation.

Tạo tag/release v1.0.0.

Tạo và đính kèm artifact cuối.