// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.cuchi.mat

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNhamHaiMat

class NhanDienNhamHaiMat(
    private val cauHinh: CauHinhNhamHaiMat =
        CauHinhNhamHaiMat(),
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

    private var batDauMatDuLieuMs: Long? =
        null

    private var daDongRoTrongLanNham =
        false

    fun capNhat(
        doNhamMatTrai: Float?,
        doNhamMatPhai: Float?,
        thoiGianMs: Long,
        thoiGianXacNhanMs: Long =
            cauHinh.thoiGianNhamXacNhanMs
    ) {

        if (
            doNhamMatTrai == null ||
            doNhamMatPhai == null
        ) {
            xuLyMatDuLieu(
                thoiGianMs
            )
            return
        }

        ketThucMatDuLieuNeuCan(
            thoiGianMs
        )

        val nguongBatDauNham =
            layNguongBatDauNham()

        val traiDongRo =
            doNhamMatTrai >=
                    cauHinh.nguongDong

        val phaiDongRo =
            doNhamMatPhai >=
                    cauHinh.nguongDong

        val traiDangNham =
            doNhamMatTrai >=
                    nguongBatDauNham

        val phaiDangNham =
            doNhamMatPhai >=
                    nguongBatDauNham

        val traiMo =
            doNhamMatTrai <=
                    cauHinh.nguongMo

        val phaiMo =
            doNhamMatPhai <=
                    cauHinh.nguongMo

        val caHaiDongRo =
            traiDongRo &&
                    phaiDongRo

        val caHaiDangNham =
            traiDangNham &&
                    phaiDangNham

        val caHaiMo =
            traiMo &&
                    phaiMo

        val coMotMatMo =
            traiMo ||
                    phaiMo

        when (trangThai) {

            TrangThai.CHO_MO ->
                xuLyChoMo(
                    caHaiMo = caHaiMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.SAN_SANG ->
                xuLySanSang(
                    caHaiDangNham = caHaiDangNham,
                    caHaiDongRo = caHaiDongRo,
                    caHaiMo = caHaiMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.DANG_NHAM ->
                xuLyDangNham(
                    caHaiDangNham = caHaiDangNham,
                    caHaiDongRo = caHaiDongRo,
                    caHaiMo = caHaiMo,
                    coMotMatMo = coMotMatMo,
                    thoiGianMs = thoiGianMs,
                    thoiGianXacNhanMs =
                        thoiGianXacNhanMs
                            .coerceAtLeast(1L)
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

        batDauMatDuLieuMs =
            null

        daDongRoTrongLanNham =
            false
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
            thoiGianMs - batDau >=
            cauHinh.thoiGianMoDeRearmMs
        ) {
            trangThai =
                TrangThai.SAN_SANG

            batDauMoOnDinhMs =
                null
        }
    }

    private fun xuLySanSang(
        caHaiDangNham: Boolean,
        caHaiDongRo: Boolean,
        caHaiMo: Boolean,
        thoiGianMs: Long
    ) {
        if (caHaiDangNham) {
            trangThai =
                TrangThai.DANG_NHAM

            batDauNhamMs =
                thoiGianMs

            batDauNhieuMs =
                null

            daDongRoTrongLanNham =
                caHaiDongRo

            return
        }

        if (caHaiMo) {
            batDauNhieuMs =
                null
        }
    }

    private fun xuLyDangNham(
        caHaiDangNham: Boolean,
        caHaiDongRo: Boolean,
        caHaiMo: Boolean,
        coMotMatMo: Boolean,
        thoiGianMs: Long,
        thoiGianXacNhanMs: Long
    ) {
        if (caHaiDangNham) {
            batDauNhieuMs =
                null

            if (caHaiDongRo) {
                daDongRoTrongLanNham =
                    true
            }

            val batDau =
                batDauNhamMs
                    ?: thoiGianMs.also {
                        batDauNhamMs =
                            it
                    }

            if (
                daDongRoTrongLanNham &&
                thoiGianMs - batDau >=
                thoiGianXacNhanMs
            ) {
                trangThai =
                    TrangThai.DA_KICH_HOAT

                khiXacNhan()
            }

            return
        }

        if (caHaiMo) {
            chuyenSangChoMo(
                thoiGianMs
            )
            return
        }

        // Một mắt dao động ngắn không được phá lần nhắm có chủ đích.
        if (coMotMatMo || !caHaiDangNham) {
            val batDauNhieu =
                batDauNhieuMs

            if (batDauNhieu == null) {
                batDauNhieuMs =
                    thoiGianMs
                return
            }

            if (
                thoiGianMs - batDauNhieu >
                cauHinh.thoiGianNhieuChoPhepMs
            ) {
                chuyenSangChoMo()
            }
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

    private fun xuLyMatDuLieu(
        thoiGianMs: Long
    ) {
        val batDau =
            batDauMatDuLieuMs

        if (batDau == null) {
            batDauMatDuLieuMs =
                thoiGianMs
            return
        }

        if (
            thoiGianMs - batDau >
            THOI_GIAN_MAT_DU_LIEU_CHO_PHEP_MS
        ) {
            datLai()
        }
    }

    private fun ketThucMatDuLieuNeuCan(
        thoiGianMs: Long
    ) {
        val batDauMatDuLieu =
            batDauMatDuLieuMs
                ?: return

        val khoangMatDuLieu =
            (thoiGianMs - batDauMatDuLieu)
                .coerceAtLeast(0L)

        // Không tính thời gian mất tracking vào thời gian giữ mắt.
        if (
            trangThai == TrangThai.DANG_NHAM &&
            khoangMatDuLieu <=
            THOI_GIAN_MAT_DU_LIEU_CHO_PHEP_MS
        ) {
            batDauNhamMs =
                batDauNhamMs
                    ?.plus(
                        khoangMatDuLieu
                    )
        }

        batDauMatDuLieuMs =
            null
    }

    private fun layNguongBatDauNham(): Float {
        val khoangCach =
            cauHinh.nguongDong -
                    cauHinh.nguongMo

        if (khoangCach <= 0f) {
            return cauHinh.nguongDong
        }

        return cauHinh.nguongMo +
                khoangCach *
                TY_LE_BAT_DAU_NHAM
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

        batDauMatDuLieuMs =
            null

        daDongRoTrongLanNham =
            false
    }

    companion object {
        const val THOI_GIAN_NHAM_XAC_NHAN_MS =
            400L

        const val THOI_GIAN_MO_DE_REARM_MS =
            150L

        private const val TY_LE_BAT_DAU_NHAM =
            0.45f

        private const val THOI_GIAN_MAT_DU_LIEU_CHO_PHEP_MS =
            150L
    }
}
