<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Third-Party Notices

FaceAccess v2 sử dụng các dependency bên thứ ba thông qua Gradle. Mã nguồn FaceAccess mang giấy phép MIT, nhưng dependency/model bên thứ ba không bị đổi giấy phép bởi dự án.

Thành phần

Version trong project

Vai trò

License / trạng thái

AndroidX Core KTX

1.19.0

Android utilities

Apache-2.0

AndroidX Activity KTX

1.13.0

Activity APIs

Apache-2.0

AndroidX AppCompat

1.6.1

UI compatibility

Apache-2.0

CameraX core/camera2/lifecycle/view

1.6.1

Camera pipeline

Apache-2.0

Material Components for Android

1.10.0

UI components

Apache-2.0

MediaPipe Tasks Vision

1.0.0

Face Landmarker runtime

Apache-2.0

uCrop

2.2.11

Crop ảnh liên hệ

Apache-2.0

Lottie Android

6.7.1

Animation

Apache-2.0

JUnit 4

4.13.2

Unit test only

EPL-1.0

AndroidX JUnit / Espresso

theo libs.versions.toml

Instrumentation test

Apache-2.0

MediaPipe Face Landmarker model

Project hiện chứa model:

app/src/main/assets/face_landmarker (1).task

MediaPipe framework và sample code được công bố theo Apache-2.0. Tuy nhiên, trước bản phát hành dự thi chính thức, nhóm cần xác nhận và lưu bằng chứng provenance/license cho chính xác model artifact đang bundle, ví dụ nguồn tải chính thức, version và SHA-256. Không nên chỉ suy luận giấy phép model từ giấy phép của thư viện runtime.

Khuyến nghị trước release:

Tải lại model từ nguồn MediaPipe chính thức nếu cần.

Ghi URL/version nguồn tải.

Tính SHA-256 của file model đang bundle.

Lưu thông tin này trong tài liệu release/third-party notices.

Lưu ý phát hành

Không copy mã nguồn của dependency vào repository FaceAccess nếu không cần thiết. Sử dụng dependency manager (Gradle) và giữ nguyên thông báo bản quyền/giấy phép của upstream.