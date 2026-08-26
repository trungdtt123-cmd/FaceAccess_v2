package com.example.faceaccess.v2.dieuphoi.media

/**
 * Các lệnh semantic của chế độ MEDIA.
 *
 * Detector chỉ phát HuongDau.
 * DieuPhoiCuChi mới ánh xạ HuongDau -> LenhMedia.
 */
enum class LenhMedia {

    /**
     * Media trước đó.
     */
    TRUOC,

    /**
     * Media tiếp theo.
     */
    TIEP_THEO,

    /**
     * Tăng âm lượng media.
     */
    TANG_AM_LUONG,

    /**
     * Giảm âm lượng media.
     */
    GIAM_AM_LUONG
}
