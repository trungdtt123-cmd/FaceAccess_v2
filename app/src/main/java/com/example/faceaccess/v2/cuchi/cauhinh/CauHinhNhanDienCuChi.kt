package com.example.faceaccess.v2.cuchi.cauhinh

// Cấu hình chung cho toàn bộ nhận diện cử chỉ
data class CauHinhNhanDienCuChi(

    val chuanHoa: CauHinhChuanHoa =
        CauHinhChuanHoa(),

    val huongDau: CauHinhHuongDau =
        CauHinhHuongDau(),

    val nghiengDau: CauHinhNghiengDau =
        CauHinhNghiengDau(),

    val nhamHaiMat: CauHinhNhamHaiMat =
        CauHinhNhamHaiMat(),

    val moMieng: CauHinhMoMieng =
        CauHinhMoMieng(),

    val moMiengHaiLan: CauHinhMoMiengHaiLan =
        CauHinhMoMiengHaiLan()

) {

    companion object {

        // Trả về bộ cấu hình mặc định
        fun macDinh(): CauHinhNhanDienCuChi {
            return CauHinhNhanDienCuChi()
        }
    }
}


// Giá trị trung tính dùng để bù lệch tư thế khuôn mặt
data class CauHinhChuanHoa(

    val lechRollTrungTinh: Float =
        0f,

    val lechYawTrungTinh: Float =
        0f,

    val lechPitchTrungTinh: Float =
        0f
)


// Cấu hình quay, ngẩng và cúi đầu
data class CauHinhHuongDau(

    // Ngưỡng quay đầu trái/phải
    val nguongYaw: Float =
        16f,

    // Ngưỡng ngẩng/cúi đầu
    val nguongPitch: Float =
        11f,

    // Tỷ lệ ưu tiên chuyển động ngang
    val tyLeChiPhoiYaw: Float =
        1.05f,

    // Tỷ lệ ưu tiên chuyển động dọc
    val tyLeChiPhoiPitch: Float =
        1.05f,

    // Ngưỡng roll được xem là trung tính
    val nguongRollTrungTinh: Float =
        8f,

    // Ngưỡng yaw được xem là trung tính
    val nguongYawTrungTinh: Float =
        11f,

    // Ngưỡng pitch được xem là trung tính
    val nguongPitchTrungTinh: Float =
        9f,

    // Thời gian giữ cử chỉ quay đầu
    val thoiGianGiuYawMs: Long =
        110L,

    // Thời gian giữ cử chỉ ngẩng/cúi
    val thoiGianGiuPitchMs: Long =
        120L,

    // Thời gian cho phép dao động ngắn
    val thoiGianGraceMs: Long =
        200L,

    // Thời gian giữ trạng thái trung tính
    val thoiGianTrungTinhMs: Long =
        80L
)


// Cấu hình nhận diện nghiêng đầu
data class CauHinhNghiengDau(

    // Ngưỡng nghiêng trái
    val nguongTrai: Float =
        -16f,

    // Ngưỡng nghiêng phải
    val nguongPhai: Float =
        16f,

    // Ngưỡng trở về trung tính
    val nguongTrungTinh: Float =
        7f,

    // Thời gian giữ cử chỉ
    val thoiGianGiuMs: Long =
        260L,

    // Tỷ lệ ưu tiên chuyển động nghiêng
    val tyLeChiPhoi: Float =
        0.75f
)


// Cấu hình nhận diện nhắm hai mắt
data class CauHinhNhamHaiMat(

    // Ngưỡng xác định mắt đóng
    val nguongDong: Float =
        0.65f,

    // Ngưỡng xác định mắt mở
    val nguongMo: Float =
        0.35f,

    // Thời gian nhắm để xác nhận cử chỉ
    val thoiGianNhamXacNhanMs: Long =
        THOI_GIAN_NHAM_XAC_NHAN_MAC_DINH_MS,

    // Thời gian mở mắt để nhận cử chỉ tiếp theo
    val thoiGianMoDeRearmMs: Long =
        THOI_GIAN_MO_DE_REARM_MAC_DINH_MS,

    // Thời gian cho phép nhiễu
    val thoiGianNhieuChoPhepMs: Long =
        100L

) {

    companion object {

        // Thời gian nhắm mặc định
        const val THOI_GIAN_NHAM_XAC_NHAN_MAC_DINH_MS =
            400L

        // Thời gian mở lại mặc định
        const val THOI_GIAN_MO_DE_REARM_MAC_DINH_MS =
            150L
    }
}


// Cấu hình nhận diện mở miệng giữ
data class CauHinhMoMieng(

    // Ngưỡng xác định miệng mở
    val nguongMo: Float =
        0.35f,

    // Ngưỡng xác định miệng đóng
    val nguongDong: Float =
        0.10f,

    // Thời gian giữ để thực hiện Back
    val thoiGianGiuBackMs: Long =
        500L,

    // Thời gian đóng miệng để nhận lại cử chỉ
    val thoiGianDongDeRearmMs: Long =
        150L
)


// Cấu hình nhận diện mở miệng hai lần
data class CauHinhMoMiengHaiLan(

    // Ngưỡng xác định miệng mở
    val nguongMo: Float =
        0.30f,

    // Ngưỡng xác định miệng đóng
    val nguongDong: Float =
        0.18f,

    // Thời gian giữ để thực hiện Back
    val thoiGianGiuBackMs: Long =
        500L,

    // Thời gian mở tối thiểu
    val thoiGianMoNganToiThieuMs: Long =
        60L,

    // Khoảng chờ lần mở thứ hai
    val khoangChoLanHaiMs: Long =
        700L,

    // Thời gian đóng để nhận lại cử chỉ
    val thoiGianDongDeRearmMs: Long =
        120L,

    // Thời gian cho phép nhiễu
    val thoiGianNhieuChoPhepMs: Long =
        140L
)