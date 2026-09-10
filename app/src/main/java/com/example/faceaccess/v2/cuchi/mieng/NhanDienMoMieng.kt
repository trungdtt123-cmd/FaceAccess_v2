// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.cuchi.mieng

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhMoMieng

class NhanDienMoMieng(

    // Cấu hình nhận diện mở miệng
    private val cauHinh: CauHinhMoMieng =
        CauHinhMoMieng(),

    // Callback khi nhận diện thành công
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

        // Thiếu dữ liệu thì reset
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

        if (doMoMieng < cauHinh.nguongMo) {
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

        if (doMoMieng <= cauHinh.nguongDong) {
            datLai()
            return
        }

        if (doMoMieng < cauHinh.nguongMo) {
            return
        }

        val thoiGianDaMo =
            thoiGianMs -
                    thoiDiemBatDauMo

        if (
            thoiGianDaMo <
            cauHinh.thoiGianGiuBackMs
        ) {
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

        if (doMoMieng > cauHinh.nguongDong) {

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
            thoiGianMs -
                    thoiDiemBatDauDong

        if (
            thoiGianDaDong >=
            cauHinh.thoiGianDongDeRearmMs
        ) {

            datLai()
        }
    }
}