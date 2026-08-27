package com.example.faceaccess.v2.cuchi.mat

class NhanDienNhamHaiMat(
    private val khiXacNhan: () -> Unit
) {

    private enum class TrangThai {
        CHO_MO,
        SAN_SANG,
        DANG_NHAM,
        DA_KICH_HOAT
    }

    private var trangThai =
        TrangThai.CHO_MO

    private var batDauMoOnDinhMs: Long? =
        null

    private var batDauNhamMs: Long? =
        null

    private var batDauNhieuMs: Long? =
        null

    fun capNhat(
        doNhamMatTrai: Float?,
        doNhamMatPhai: Float?,
        thoiGianMs: Long
    ) {
        if (
            doNhamMatTrai == null ||
            doNhamMatPhai == null
        ) {
            datLai()
            return
        }

        val traiDong =
            doNhamMatTrai >= NGUONG_DONG

        val phaiDong =
            doNhamMatPhai >= NGUONG_DONG

        val traiMo =
            doNhamMatTrai <= NGUONG_MO

        val phaiMo =
            doNhamMatPhai <= NGUONG_MO

        val caHaiDong =
            traiDong &&
                    phaiDong

        val caHaiMo =
            traiMo &&
                    phaiMo

        when (trangThai) {
            TrangThai.CHO_MO ->
                xuLyChoMo(
                    caHaiMo = caHaiMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.SAN_SANG ->
                xuLySanSang(
                    caHaiDong = caHaiDong,
                    caHaiMo = caHaiMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.DANG_NHAM ->
                xuLyDangNham(
                    caHaiDong = caHaiDong,
                    caHaiMo = caHaiMo,
                    coMatMo = traiMo || phaiMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.DA_KICH_HOAT ->
                xuLyDaKichHoat(
                    caHaiMo = caHaiMo,
                    thoiGianMs = thoiGianMs
                )
        }
    }

    fun datLai() {
        trangThai =
            TrangThai.CHO_MO

        batDauMoOnDinhMs =
            null

        batDauNhamMs =
            null

        batDauNhieuMs =
            null
    }

    private fun xuLyChoMo(
        caHaiMo: Boolean,
        thoiGianMs: Long
    ) {
        if (!caHaiMo) {
            batDauMoOnDinhMs =
                null
            return
        }

        val batDau =
            batDauMoOnDinhMs

        if (batDau == null) {
            batDauMoOnDinhMs =
                thoiGianMs
            return
        }

        if (
            thoiGianMs -
            batDau >=
            THOI_GIAN_MO_DE_REARM_MS
        ) {
            trangThai =
                TrangThai.SAN_SANG

            batDauMoOnDinhMs =
                null
        }
    }

    private fun xuLySanSang(
        caHaiDong: Boolean,
        caHaiMo: Boolean,
        thoiGianMs: Long
    ) {
        if (caHaiDong) {
            trangThai =
                TrangThai.DANG_NHAM

            batDauNhamMs =
                thoiGianMs

            batDauNhieuMs =
                null

            return
        }

        if (caHaiMo) {
            batDauNhieuMs =
                null
        }
    }

    private fun xuLyDangNham(
        caHaiDong: Boolean,
        caHaiMo: Boolean,
        coMatMo: Boolean,
        thoiGianMs: Long
    ) {
        if (caHaiDong) {
            batDauNhieuMs =
                null

            val batDau =
                batDauNhamMs
                    ?: thoiGianMs
                        .also {
                            batDauNhamMs =
                                it
                        }

            if (
                thoiGianMs -
                batDau >=
                THOI_GIAN_NHAM_XAC_NHAN_MS
            ) {
                trangThai =
                    TrangThai.DA_KICH_HOAT

                khiXacNhan()
            }

            return
        }

        if (caHaiMo || coMatMo) {
            chuyenSangChoMo(
                thoiGianMs =
                    if (caHaiMo) {
                        thoiGianMs
                    } else {
                        null
                    }
            )
            return
        }

        val batDauNhieu =
            batDauNhieuMs

        if (batDauNhieu == null) {
            batDauNhieuMs =
                thoiGianMs
            return
        }

        if (
            thoiGianMs -
            batDauNhieu >
            THOI_GIAN_NHIEU_CHO_PHEP_MS
        ) {
            chuyenSangChoMo()
        }
    }

    private fun xuLyDaKichHoat(
        caHaiMo: Boolean,
        thoiGianMs: Long
    ) {
        if (!caHaiMo) {
            return
        }

        chuyenSangChoMo(
            thoiGianMs
        )
    }

    private fun chuyenSangChoMo(
        thoiGianMs: Long? = null
    ) {
        trangThai =
            TrangThai.CHO_MO

        batDauMoOnDinhMs =
            thoiGianMs

        batDauNhamMs =
            null

        batDauNhieuMs =
            null
    }

    companion object {
        const val THOI_GIAN_NHAM_XAC_NHAN_MS =
            400L

        const val THOI_GIAN_MO_DE_REARM_MS =
            150L

        private const val THOI_GIAN_NHIEU_CHO_PHEP_MS =
            100L

        private const val NGUONG_DONG =
            0.65f

        private const val NGUONG_MO =
            0.35f
    }
}