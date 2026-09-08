
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.cuchi.cauhinh

data class CauHinhNhanDienCuChi(
    val chuanHoa: CauHinhChuanHoa = CauHinhChuanHoa(),
    val huongDau: CauHinhHuongDau = CauHinhHuongDau(),
    val nghiengDau: CauHinhNghiengDau = CauHinhNghiengDau(),
    val nhamHaiMat: CauHinhNhamHaiMat = CauHinhNhamHaiMat(),
    val moMieng: CauHinhMoMieng = CauHinhMoMieng(),
    val moMiengHaiLan: CauHinhMoMiengHaiLan = CauHinhMoMiengHaiLan(),
    val hanhDong: CauHinhHanhDongCuChi = CauHinhHanhDongCuChi()
) {

    fun datLaiHuongDau() =
        copy(
            huongDau = CauHinhHuongDau()
        )

    fun datLaiNghiengDau() =
        copy(
            nghiengDau = CauHinhNghiengDau()
        )

    fun datLaiNhamHaiMat() =
        copy(
            nhamHaiMat = CauHinhNhamHaiMat()
        )

    fun datLaiMoMieng() =
        copy(
            moMieng = CauHinhMoMieng()
        )

    fun datLaiMoMiengHaiLan() =
        copy(
            moMiengHaiLan = CauHinhMoMiengHaiLan()
        )

    fun datLaiThongSoNhanDien() =
        copy(
            huongDau = CauHinhHuongDau(),
            nghiengDau = CauHinhNghiengDau(),
            nhamHaiMat = CauHinhNhamHaiMat(),
            moMieng = CauHinhMoMieng(),
            moMiengHaiLan = CauHinhMoMiengHaiLan()
        )

    fun datLaiHanhDong() =
        copy(
            hanhDong = CauHinhHanhDongCuChi()
        )

    companion object {
        fun macDinh(): CauHinhNhanDienCuChi =
            CauHinhNhanDienCuChi()
    }
}

data class CauHinhChuanHoa(
    val lechRollTrungTinh: Float = 0f,
    val lechYawTrungTinh: Float = 0f,
    val lechPitchTrungTinh: Float = 0f
)

data class CauHinhHuongDau(
    val nguongYaw: Float = 16f,
    val nguongPitch: Float = 11f,
    val tyLeChiPhoiYaw: Float = 1.05f,
    val tyLeChiPhoiPitch: Float = 1.05f,
    val nguongRollTrungTinh: Float = 8f,
    val nguongYawTrungTinh: Float = 11f,
    val nguongPitchTrungTinh: Float = 9f,
    val thoiGianGiuYawMs: Long = 110L,
    val thoiGianGiuPitchMs: Long = 120L,
    val thoiGianGraceMs: Long = 200L,
    val thoiGianTrungTinhMs: Long = 80L,

    // null = dùng ngưỡng nhóm cũ
    val nguongQuayTrai: Float? = null,
    val nguongQuayPhai: Float? = null,
    val nguongNhinLen: Float? = null,
    val nguongNhinXuong: Float? = null
) {

    fun layNguongQuayTrai(): Float =
        nguongQuayTrai ?: nguongYaw

    fun layNguongQuayPhai(): Float =
        nguongQuayPhai ?: nguongYaw

    fun layNguongNhinLen(): Float =
        nguongNhinLen ?: nguongPitch

    fun layNguongNhinXuong(): Float =
        nguongNhinXuong ?: nguongPitch
}

data class CauHinhNghiengDau(
    val nguongTrai: Float = -16f,
    val nguongPhai: Float = 16f,
    val nguongTrungTinh: Float = 7f,
    val thoiGianGiuMs: Long = 260L,
    val tyLeChiPhoi: Float = 0.75f
)

data class CauHinhNhamHaiMat(
    val nguongDong: Float = 0.65f,
    val nguongMo: Float = 0.35f,
    val thoiGianNhamXacNhanMs: Long =
        THOI_GIAN_NHAM_XAC_NHAN_MAC_DINH_MS,
    val thoiGianMoDeRearmMs: Long =
        THOI_GIAN_MO_DE_REARM_MAC_DINH_MS,
    val thoiGianNhieuChoPhepMs: Long = 100L
) {
    companion object {
        const val THOI_GIAN_NHAM_XAC_NHAN_MAC_DINH_MS =
            400L

        const val THOI_GIAN_MO_DE_REARM_MAC_DINH_MS =
            150L
    }
}

data class CauHinhMoMieng(
    val nguongMo: Float = 0.35f,
    val nguongDong: Float = 0.10f,
    val thoiGianGiuBackMs: Long = 500L,
    val thoiGianDongDeRearmMs: Long = 150L
)

data class CauHinhMoMiengHaiLan(
    val nguongMo: Float = 0.30f,
    val nguongDong: Float = 0.18f,
    val thoiGianGiuBackMs: Long = 500L,
    val thoiGianMoNganToiThieuMs: Long = 60L,
    val khoangChoLanHaiMs: Long = 700L,
    val thoiGianDongDeRearmMs: Long = 120L,
    val thoiGianNhieuChoPhepMs: Long = 140L
)

data class CauHinhHanhDongCuChi(
    val quayTrai: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO,

    val quayPhai: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO,

    val nhinLen: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO,

    val nhinXuong: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO,

    val nghiengTrai: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.HOME,

    val nghiengPhai: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.DOI_CHE_DO,

    val nhamHaiMat: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO,

    val moMieng: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.BACK,

    val moMiengHaiLan: HanhDongTuyChinhCuChi =
        HanhDongTuyChinhCuChi.THEO_CHE_DO
) {

    fun chuanHoaChoCuChiToanCuc():
            CauHinhHanhDongCuChi {

        val hopLe =
            setOf(
                HanhDongTuyChinhCuChi.BACK,
                HanhDongTuyChinhCuChi.HOME,
                HanhDongTuyChinhCuChi.DOI_CHE_DO
            )

        val boBa =
            listOf(
                nghiengTrai,
                nghiengPhai,
                moMieng
            )

        val hopLeVaKhongTrung =
            boBa.all {
                it in hopLe
            } &&
                    boBa.toSet().size == 3

        val hanhDongNghiengTrai =
            if (hopLeVaKhongTrung) {
                nghiengTrai
            } else {
                HanhDongTuyChinhCuChi.HOME
            }

        val hanhDongNghiengPhai =
            if (hopLeVaKhongTrung) {
                nghiengPhai
            } else {
                HanhDongTuyChinhCuChi.DOI_CHE_DO
            }

        val hanhDongMoMieng =
            if (hopLeVaKhongTrung) {
                moMieng
            } else {
                HanhDongTuyChinhCuChi.BACK
            }

        return copy(
            quayTrai =
                HanhDongTuyChinhCuChi.THEO_CHE_DO,
            quayPhai =
                HanhDongTuyChinhCuChi.THEO_CHE_DO,
            nhinLen =
                HanhDongTuyChinhCuChi.THEO_CHE_DO,
            nhinXuong =
                HanhDongTuyChinhCuChi.THEO_CHE_DO,
            nghiengTrai =
                hanhDongNghiengTrai,
            nghiengPhai =
                hanhDongNghiengPhai,
            nhamHaiMat =
                HanhDongTuyChinhCuChi.THEO_CHE_DO,
            moMieng =
                hanhDongMoMieng,
            moMiengHaiLan =
                HanhDongTuyChinhCuChi.THEO_CHE_DO
        )
    }
}

enum class HanhDongTuyChinhCuChi(
    val tenHienThi: String
) {
    THEO_CHE_DO("THEO CHẾ ĐỘ"),
    CLICK("CLICK"),
    BACK("BACK"),
    HOME("HOME"),
    CUON_LEN("CUỘN LÊN"),
    CUON_XUONG("CUỘN XUỐNG"),
    DOI_CHE_DO("ĐỔI CHẾ ĐỘ"),
    DOI_KHOA_CON_TRO("BẬT/TẮT CON TRỎ"),
    KHONG_SU_DUNG("KHÔNG SỬ DỤNG")
}