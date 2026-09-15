<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Open Source Competition Checklist

Checklist tự kiểm cuối cùng của FaceAccess v2 sau khi phát hành v1.0.0.

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

Đã tạo Git tag v1.0.0.

Đã push tag v1.0.0 lên GitHub.

Đã tạo GitHub Release v1.0.0.

Release artifacts

Có APK phát hành: FaceAccess-v1.0.0-debug.apk.

Có source archive: FaceAccess-v1.0.0-source.tar.gz.

Có SHA256SUMS.txt.

Có release notes.

SHA-256 APK đã được ghi:
28291217eef24dc750744728195c7852d8362fdb15ae7ef4d37043b1ea168813

SHA-256 source archive đã được ghi:
08c2d4a6ef0eca4c87fd84e80fc2068c7fc93e96b662cf2c3d301225fb55c7a0

SHA-256 model Face Landmarker đã được ghi trong tài liệu third-party.

Build from source

Có Gradle Wrapper.

README có hướng dẫn build.

Có docs/BUILDING.md.

Local clean thành công.

Local testDebugUnitTest thành công.

Local assembleDebug thành công.

Debug APK được tạo thành công.

Có GitHub Actions build/test.

GitHub Actions Android CI đã chạy xanh.

Commit chuẩn bị release v1.0.0 đã chạy CI xanh.

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

MediaPipe Face Landmarker được mô tả đúng là pretrained ML inference.

Có tài liệu mô tả gesture logic và adaptive calibration.

Nếu triển khai custom AI trong tương lai: công bố dataset/features, training code, metrics và model artifact.

Final Release Gate

Local build/test xanh.

GitHub Actions xanh.

Compatibility/testing documentation đã đồng bộ.

License/model provenance đã khóa.

Version 1.0.0 đã commit và push.

Git tag v1.0.0 đã tạo và push.

GitHub Release v1.0.0 đã publish.

APK, source .tar.gz và checksum đã được đính kèm.

Release notes đã publish.

Thực hiện rà soát cuối toàn bộ repository trước thời điểm nộp bài.

Chuẩn bị showcase/demo cuối cùng cho vòng chung kết.