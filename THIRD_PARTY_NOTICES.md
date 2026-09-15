<!-- SPDX-License-Identifier: MIT -->

<!-- Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung -->

Third-Party Notices

FaceAccess v2 sử dụng các dependency bên thứ ba thông qua Gradle. Mã nguồn do nhóm phát triển mang giấy phép MIT. Các thư viện, model và thành phần bên thứ ba giữ nguyên giấy phép của chủ sở hữu tương ứng.

Dependencies

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

Model đang được bundle trong project:

app/src/main/assets/face_landmarker (1).task

FaceAccess sử dụng model bundle Face Landmarker chính thức của Google MediaPipe. Theo tài liệu MediaPipe, bundle này gồm các thành phần Face Detector (BlazeFace short range), FaceMesh-V2 và Blendshape.

Provenance đã xác minh

Nguồn chính thức: Google MediaPipe model storage.

Endpoint được dùng để đối chiếu:
https://storage.googleapis.com/mediapipe-models/face_landmarker/face_landmarker/float16/latest/face_landmarker.task

Ngày xác minh: 2026-09-15.

SHA-256 của model đang bundle:
64184E229B263107BC2B804C6625DB1341FF2BB731874B0BCC2FE6544E0BC9FF

SHA-256 của model tải trực tiếp từ endpoint chính thức ở trên:
64184E229B263107BC2B804C6625DB1341FF2BB731874B0BCC2FE6544E0BC9FF

Kết luận: hai artifact trùng khớp byte-for-byte tại thời điểm xác minh.

License của các model thành phần

Các model card chính thức của MediaPipe ghi Apache License, Version 2.0 cho:

MediaPipe BlazeFace Short Range.

MediaPipe FaceMesh V2.

MediaPipe Blendshape V2.

FaceAccess không sửa đổi model bundle này.

Lưu ý phát hành

Không copy mã nguồn dependency vào repository FaceAccess nếu không cần thiết. Sử dụng dependency manager (Gradle), giữ nguyên thông báo bản quyền/giấy phép của upstream và giữ lại thông tin provenance + checksum của model trong tài liệu phát hành.