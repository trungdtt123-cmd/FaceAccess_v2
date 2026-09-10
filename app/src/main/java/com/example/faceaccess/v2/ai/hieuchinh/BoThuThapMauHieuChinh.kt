// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.ai.hieuchinh

import com.example.faceaccess.v2.khuonmat.DuLieuKhuonMat

class BoThuThapMauHieuChinh(

    // Số mẫu cần thu cho mỗi bước
    private val soMauMucTieuMoiBuoc: Int = 20

) {

    private val mauTheoBuoc:
            MutableMap<BuocHieuChinh, MutableList<DuLieuKhuonMat>> =
        mutableMapOf()

    private var buocHienTai:
            BuocHieuChinh? = null


    // Bắt đầu thu mẫu cho một bước
    fun batDauBuoc(
        buoc: BuocHieuChinh
    ) {

        buocHienTai =
            buoc

        mauTheoBuoc[buoc] =
            mutableListOf()
    }


    // Thêm một mẫu hợp lệ
    fun themMau(
        duLieu: DuLieuKhuonMat
    ): Boolean {

        val buoc =
            buocHienTai
                ?: return false

        if (!mauHopLe(buoc, duLieu)) {
            return false
        }

        val danhSach:
                MutableList<DuLieuKhuonMat> =
            mauTheoBuoc.getOrPut(buoc) {
                mutableListOf()
            }

        if (
            danhSach.size >=
            soMauMucTieuMoiBuoc
        ) {
            return false
        }

        danhSach.add(
            duLieu
        )

        return true
    }


    // Kiểm tra bước hiện tại đã đủ mẫu
    fun daDuMauBuocHienTai(): Boolean {

        val buoc =
            buocHienTai
                ?: return false

        return soMauDaThu(buoc) >=
                soMauMucTieuMoiBuoc
    }


    // Lấy số mẫu đã thu
    fun soMauDaThu(
        buoc: BuocHieuChinh
    ): Int {

        return mauTheoBuoc[buoc]
            ?.size
            ?: 0
    }


    // Lấy số mẫu mục tiêu
    fun soMauMucTieu(): Int {

        return soMauMucTieuMoiBuoc
    }


    // Lấy tiến độ từ 0 đến 1
    fun tienDoBuocHienTai(): Float {

        val buoc =
            buocHienTai
                ?: return 0f

        val soMau =
            soMauDaThu(buoc)

        return (
                soMau.toFloat() /
                        soMauMucTieuMoiBuoc.toFloat()
                )
            .coerceIn(
                0f,
                1f
            )
    }


    // Lấy dữ liệu của một bước
    fun layMau(
        buoc: BuocHieuChinh
    ): List<DuLieuKhuonMat> {

        return mauTheoBuoc[buoc]
            ?.toList()
            ?: emptyList()
    }


    // Kết thúc bước đang thu
    fun ketThucBuoc() {

        buocHienTai =
            null
    }


    // Kiểm tra tất cả bước đã đủ mẫu
    fun daDuTatCaMau(): Boolean {

        return BuocHieuChinh
            .values()
            .all { buoc ->

                soMauDaThu(buoc) >=
                        soMauMucTieuMoiBuoc
            }
    }


    // Xóa toàn bộ dữ liệu hiệu chỉnh
    fun datLai() {

        mauTheoBuoc.clear()

        buocHienTai =
            null
    }


    // Kiểm tra mẫu có đủ dữ liệu cần thiết
    private fun mauHopLe(
        buoc: BuocHieuChinh,
        duLieu: DuLieuKhuonMat
    ): Boolean {

        return when (buoc) {

            BuocHieuChinh.TRUNG_TINH -> {

                duLieu.roll != null &&
                        duLieu.yaw != null &&
                        duLieu.pitch != null &&
                        duLieu.doNhamMatTrai != null &&
                        duLieu.doNhamMatPhai != null &&
                        duLieu.doMoMieng != null
            }


            BuocHieuChinh.QUAY_TRAI,
            BuocHieuChinh.QUAY_PHAI,
            BuocHieuChinh.NHIN_LEN,
            BuocHieuChinh.NHIN_XUONG,
            BuocHieuChinh.NGHIENG_TRAI,
            BuocHieuChinh.NGHIENG_PHAI -> {

                duLieu.roll != null &&
                        duLieu.yaw != null &&
                        duLieu.pitch != null
            }


            BuocHieuChinh.NHAM_HAI_MAT -> {

                duLieu.doNhamMatTrai != null &&
                        duLieu.doNhamMatPhai != null
            }


            BuocHieuChinh.MO_MIENG -> {

                duLieu.doMoMieng != null
            }
        }
    }
}