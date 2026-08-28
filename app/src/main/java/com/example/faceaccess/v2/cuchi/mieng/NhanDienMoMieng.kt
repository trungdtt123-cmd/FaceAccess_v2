package com.example.faceaccess.v2.cuchi.mieng

class NhanDienMoMieng(
    private val khiNhanDienMoMieng: () -> Unit
) {

    private enum class TrangThai {
        SAN_SANG,
        DANG_MO,
        DA_KICH_HOAT
    }

    private var trangThai =
        TrangThai.SAN_SANG

    private var thoiDiemBatDauMo =
        0L

    private var thoiDiemBatDauDong =
        0L

    fun capNhat(
        doMoMieng: Float?,
        thoiGianMs: Long
    ) {
        if (doMoMieng == null) {
            datLai()
            return
        }

        when (trangThai) {
            TrangThai.SAN_SANG ->
                xuLySanSang(
                    doMoMieng = doMoMieng,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.DANG_MO ->
                xuLyDangMo(
                    doMoMieng = doMoMieng,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.DA_KICH_HOAT ->
                xuLySauKichHoat(
                    doMoMieng = doMoMieng,
                    thoiGianMs = thoiGianMs
                )
        }
    }

    fun datLai() {
        trangThai =
            TrangThai.SAN_SANG

        thoiDiemBatDauMo =
            0L

        thoiDiemBatDauDong =
            0L
    }

    private fun xuLySanSang(
        doMoMieng: Float,
        thoiGianMs: Long
    ) {
        if (doMoMieng < NGUONG_MO) {
            return
        }

        thoiDiemBatDauMo =
            thoiGianMs

        trangThai =
            TrangThai.DANG_MO
    }

    private fun xuLyDangMo(
        doMoMieng: Float,
        thoiGianMs: Long
    ) {
        if (doMoMieng <= NGUONG_DONG) {
            datLai()
            return
        }

        if (doMoMieng < NGUONG_MO) {
            return
        }

        val thoiGianDaMo =
            thoiGianMs - thoiDiemBatDauMo

        if (thoiGianDaMo < THOI_GIAN_GIU_BACK_MS) {
            return
        }

        khiNhanDienMoMieng()

        trangThai =
            TrangThai.DA_KICH_HOAT

        thoiDiemBatDauDong =
            0L
    }

    private fun xuLySauKichHoat(
        doMoMieng: Float,
        thoiGianMs: Long
    ) {
        if (doMoMieng > NGUONG_DONG) {
            thoiDiemBatDauDong =
                0L
            return
        }

        if (thoiDiemBatDauDong == 0L) {
            thoiDiemBatDauDong =
                thoiGianMs
            return
        }

        val thoiGianDaDong =
            thoiGianMs - thoiDiemBatDauDong

        if (thoiGianDaDong >= THOI_GIAN_DONG_DE_REARM_MS) {
            datLai()
        }
    }

    companion object {
        private const val NGUONG_MO =
            0.35f

        private const val NGUONG_DONG =
            0.10f

        private const val THOI_GIAN_GIU_BACK_MS =
            500L

        private const val THOI_GIAN_DONG_DE_REARM_MS =
            150L
    }
}