package com.example.faceaccess.v2.dieuphoi.hotro

/**
 * Các lệnh semantic của chế độ HỖ TRỢ.
 *
 * Checkpoint này chỉ định nghĩa ý nghĩa lệnh.
 * Chưa thực hiện cuộc gọi / tin nhắn thật.
 */
enum class LenhHoTro {

    /**
     * Chọn người hỗ trợ trước đó.
     */
    NGUOI_TRUOC,

    /**
     * Chọn người hỗ trợ tiếp theo.
     */
    NGUOI_TIEP_THEO,

    /**
     * Xác nhận liên hệ người đang được chọn.
     */
    XAC_NHAN_LIEN_HE,

    /**
     * Hủy thao tác liên hệ hiện tại.
     */
    HUY_LIEN_HE
}