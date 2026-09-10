// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.chedo

import com.example.faceaccess.v2.thongbao.ThongBaoFaceAccess

class BoDinhTuyenCheDo(
    private val khiCheDoThayDoi:
        (CheDoDieuKhien) -> Unit
) {

    fun layCheDoHienTai():
            CheDoDieuKhien {

        return TrangThaiCheDoToanCuc
            .layCheDoHienTai()
    }

    fun chuyenCheDoTiepTheo() {

        val cheDoMoi =
            TrangThaiCheDoToanCuc
                .chuyenCheDoTiepTheo()

        hienThiThongBaoCheDo(
            cheDoMoi
        )

        khiCheDoThayDoi(
            cheDoMoi
        )
    }

    fun datLai() {

        val cheDoMoi =
            TrangThaiCheDoToanCuc
                .datLai()

        khiCheDoThayDoi(
            cheDoMoi
        )
    }

    private fun hienThiThongBaoCheDo(
        cheDo: CheDoDieuKhien
    ) {

        val noiDung =
            when (cheDo) {

                CheDoDieuKhien.DIEU_HUONG ->
                    "Đã chuyển sang chế độ Điều hướng"

                CheDoDieuKhien.MEDIA ->
                    "Đã chuyển sang chế độ Media"

                CheDoDieuKhien.HO_TRO ->
                    "Đã chuyển sang chế độ Hỗ trợ"

                CheDoDieuKhien.CON_TRO ->
                    "Đã chuyển sang chế độ Con trỏ"
            }

        ThongBaoFaceAccess.hienThi(
            noiDung
        )
    }
}