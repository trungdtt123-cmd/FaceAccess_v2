// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.cuchi.mieng

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhMoMiengHaiLan

class NhanDienMoMiengHaiLan(
    private val khiMoMotLan: () -> Unit,
    private val khiMoHaiLan: () -> Unit
) {

    // Cấu hình nhận diện mở miệng hai lần
    private var cauHinh =
        CauHinhMoMiengHaiLan()


    // Dùng khi có cấu hình cá nhân
    constructor(
        cauHinh: CauHinhMoMiengHaiLan,
        khiMoMotLan: () -> Unit,
        khiMoHaiLan: () -> Unit
    ) : this(
        khiMoMotLan = khiMoMotLan,
        khiMoHaiLan = khiMoHaiLan
    ) {
        this.cauHinh =
            cauHinh
    }


    private enum class TrangThai {
        SAN_SANG,
        MO_LAN_1,
        CHO_LAN_2,
        MO_LAN_2,
        CHO_DONG_SAU_KICH_HOAT
    }


    private var trangThai =
        TrangThai.SAN_SANG

    private var batDauMoMs: Long? =
        null

    private var batDauChoLanHaiMs: Long? =
        null

    private var batDauDongMs: Long? =
        null

    private var batDauNhieuMs: Long? =
        null

    private var lanMotDaDuDieuKien =
        false


    fun capNhat(
        doMoMieng: Float?,
        thoiGianMs: Long
    ) {

        if (doMoMieng == null) {
            datLai()
            return
        }

        val dangMo =
            doMoMieng >=
                    cauHinh.nguongMo

        val dangDong =
            doMoMieng <=
                    cauHinh.nguongDong


        when (trangThai) {

            TrangThai.SAN_SANG ->
                xuLySanSang(
                    dangMo = dangMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.MO_LAN_1 ->
                xuLyMoLanMot(
                    dangMo = dangMo,
                    dangDong = dangDong,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.CHO_LAN_2 ->
                xuLyChoLanHai(
                    dangMo = dangMo,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.MO_LAN_2 ->
                xuLyMoLanHai(
                    dangMo = dangMo,
                    dangDong = dangDong,
                    thoiGianMs = thoiGianMs
                )

            TrangThai.CHO_DONG_SAU_KICH_HOAT ->
                xuLyChoDongSauKichHoat(
                    dangDong = dangDong,
                    thoiGianMs = thoiGianMs
                )
        }
    }


    fun datLai() {

        TrangThaiCuChiMieng.huy()

        datLaiNoiBo()
    }


    private fun xuLySanSang(
        dangMo: Boolean,
        thoiGianMs: Long
    ) {

        if (!dangMo) {
            return
        }

        TrangThaiCuChiMieng.batDau()

        trangThai =
            TrangThai.MO_LAN_1

        batDauMoMs =
            thoiGianMs

        batDauNhieuMs =
            null

        lanMotDaDuDieuKien =
            false
    }


    private fun xuLyMoLanMot(
        dangMo: Boolean,
        dangDong: Boolean,
        thoiGianMs: Long
    ) {

        if (dangMo) {

            batDauNhieuMs =
                null

            val batDau =
                batDauMoMs
                    ?: thoiGianMs.also {
                        batDauMoMs = it
                    }

            val thoiGianDaMo =
                thoiGianMs -
                        batDau


            if (
                thoiGianDaMo >=
                cauHinh.thoiGianGiuBackMs
            ) {

                khiMoMotLan()

                chuyenSangChoDongSauKichHoat()

                return
            }


            if (
                thoiGianDaMo >=
                cauHinh.thoiGianMoNganToiThieuMs
            ) {

                lanMotDaDuDieuKien =
                    true
            }

            return
        }


        if (dangDong) {

            if (lanMotDaDuDieuKien) {

                trangThai =
                    TrangThai.CHO_LAN_2

                batDauChoLanHaiMs =
                    thoiGianMs

                batDauMoMs =
                    null

                batDauNhieuMs =
                    null

            } else {

                ketThucKhongKichHoat()
            }

            return
        }


        if (quaThoiGianNhieu(thoiGianMs)) {

            ketThucKhongKichHoat()
        }
    }


    private fun xuLyChoLanHai(
        dangMo: Boolean,
        thoiGianMs: Long
    ) {

        val batDauCho =
            batDauChoLanHaiMs
                ?: thoiGianMs.also {
                    batDauChoLanHaiMs = it
                }


        if (
            thoiGianMs -
            batDauCho >
            cauHinh.khoangChoLanHaiMs
        ) {

            ketThucKhongKichHoat()

            return
        }


        if (!dangMo) {
            return
        }


        trangThai =
            TrangThai.MO_LAN_2

        batDauMoMs =
            thoiGianMs

        batDauNhieuMs =
            null
    }


    private fun xuLyMoLanHai(
        dangMo: Boolean,
        dangDong: Boolean,
        thoiGianMs: Long
    ) {

        if (dangMo) {

            batDauNhieuMs =
                null

            val batDau =
                batDauMoMs
                    ?: thoiGianMs.also {
                        batDauMoMs = it
                    }


            if (
                thoiGianMs -
                batDau >=
                cauHinh.thoiGianMoNganToiThieuMs
            ) {

                khiMoHaiLan()

                chuyenSangChoDongSauKichHoat()
            }

            return
        }


        if (dangDong) {

            trangThai =
                TrangThai.CHO_LAN_2

            batDauMoMs =
                null

            batDauNhieuMs =
                null

            return
        }


        if (quaThoiGianNhieu(thoiGianMs)) {

            trangThai =
                TrangThai.CHO_LAN_2

            batDauMoMs =
                null

            batDauNhieuMs =
                null
        }
    }


    private fun xuLyChoDongSauKichHoat(
        dangDong: Boolean,
        thoiGianMs: Long
    ) {

        if (!dangDong) {

            batDauDongMs =
                null

            return
        }


        val batDau =
            batDauDongMs
                ?: thoiGianMs.also {
                    batDauDongMs = it
                }


        if (
            thoiGianMs -
            batDau >=
            cauHinh.thoiGianDongDeRearmMs
        ) {

            TrangThaiCuChiMieng
                .ketThucVoiGuard()

            datLaiNoiBo()
        }
    }


    private fun chuyenSangChoDongSauKichHoat() {

        trangThai =
            TrangThai.CHO_DONG_SAU_KICH_HOAT

        batDauMoMs =
            null

        batDauChoLanHaiMs =
            null

        batDauDongMs =
            null

        batDauNhieuMs =
            null

        lanMotDaDuDieuKien =
            false
    }


    private fun ketThucKhongKichHoat() {

        TrangThaiCuChiMieng
            .ketThucVoiGuard()

        datLaiNoiBo()
    }


    private fun datLaiNoiBo() {

        trangThai =
            TrangThai.SAN_SANG

        batDauMoMs =
            null

        batDauChoLanHaiMs =
            null

        batDauDongMs =
            null

        batDauNhieuMs =
            null

        lanMotDaDuDieuKien =
            false
    }


    private fun quaThoiGianNhieu(
        thoiGianMs: Long
    ): Boolean {

        val batDau =
            batDauNhieuMs


        if (batDau == null) {

            batDauNhieuMs =
                thoiGianMs

            return false
        }


        return thoiGianMs -
                batDau >
                cauHinh.thoiGianNhieuChoPhepMs
    }
}