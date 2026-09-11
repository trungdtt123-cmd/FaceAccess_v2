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

    // Mỗi session mới phải thấy miệng đóng trước khi nhận cử chỉ.
    private var trangThai =
        TrangThai.DA_KICH_HOAT

    private var thoiDiemBatDauMo =
        0L

    private var thoiDiemBatDauDong =
        0L

    private var thoiDiemBatDauNhieu: Long? =
        null


    fun capNhat(
        doMoMieng: Float?,
        thoiGianMs: Long
    ) {

        // Thiếu dữ liệu thì reset
        if (doMoMieng == null) {
            datLai()
            return
        }

        // Sau khi lần mở thứ nhất ngắn đã được xác nhận, chuỗi mở hai lần
        // được ưu tiên tuyệt đối. Detector mở-giữ không được phép phát BACK
        // từ lần mở thứ hai, dù người dùng giữ miệng mở lâu.
        if (TrangThaiCuChiMieng.dangChanMoGiu()) {
            chanChoDenKhiDong()
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

        // Không coi một lần mở đã bắt đầu từ session trước là cử chỉ mới.
        trangThai =
            TrangThai.DA_KICH_HOAT

        thoiDiemBatDauMo =
            0L

        thoiDiemBatDauDong =
            0L

        thoiDiemBatDauNhieu =
            null
    }


    // Khi cử chỉ mở miệng hai lần đã được xác nhận,
    // không cho detector mở giữ kích hoạt tiếp trên cùng lần mở thứ hai.
    fun chanChoDenKhiDong() {

        trangThai =
            TrangThai.DA_KICH_HOAT

        thoiDiemBatDauMo =
            0L

        thoiDiemBatDauDong =
            0L

        thoiDiemBatDauNhieu =
            null
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

        thoiDiemBatDauNhieu =
            null

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

            val batDauNhieu =
                thoiDiemBatDauNhieu

            if (batDauNhieu == null) {
                thoiDiemBatDauNhieu =
                    thoiGianMs
                return
            }

            if (
                thoiGianMs -
                batDauNhieu >
                THOI_GIAN_NHIEU_CHO_PHEP_MS
            ) {
                datLaiVeSanSang()
            }

            return
        }

        thoiDiemBatDauNhieu =
            null

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

            datLaiVeSanSang()
        }
    }


    private fun datLaiVeSanSang() {

        trangThai =
            TrangThai.SAN_SANG

        thoiDiemBatDauMo =
            0L

        thoiDiemBatDauDong =
            0L

        thoiDiemBatDauNhieu =
            null
    }


    companion object {
        private const val THOI_GIAN_NHIEU_CHO_PHEP_MS =
            140L
    }
}
