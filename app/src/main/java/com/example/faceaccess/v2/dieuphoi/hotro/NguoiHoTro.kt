package com.example.faceaccess.v2.dieuphoi.hotro

/**
 * Một liên hệ hỗ trợ do người dùng tự cấu hình trong FaceAccess.
 *
 * - moTa chỉ hiển thị ở màn hình chi tiết.
 * - anhUri để sẵn cho phần chọn ảnh đại diện thật sau này.
 * - Khi anhUri == null, UI dùng chữ cái đầu của tên làm avatar.
 */
data class NguoiHoTro(
    val id: Long,
    val ten: String,
    val soDienThoai: String,
    val moTa: String = "",
    val anhUri: String? = null
)
