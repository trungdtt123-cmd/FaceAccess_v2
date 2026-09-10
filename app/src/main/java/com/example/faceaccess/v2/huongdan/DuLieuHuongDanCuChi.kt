// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.huongdan

import com.example.faceaccess.v2.R

enum class CheDoHuongDan {
    DIEU_HUONG,
    MEDIA,
    HO_TRO,
    CON_TRO
}

object DuLieuHuongDanCuChi {

    fun taoDanhSach(
        cheDo: CheDoHuongDan
    ): List<MucHuongDanCuChi> {

        return listOf(
            taoQuayTrai(cheDo),
            taoQuayPhai(cheDo),
            taoNhinLen(cheDo),
            taoNhinXuong(cheDo),
            taoNghiengTrai(cheDo),
            taoNghiengPhai(cheDo),
            taoNhamMat(cheDo),
            taoMoMieng(cheDo)
        )
    }

    private fun taoQuayTrai(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 1,
            tenCuChi = "Quay trái",
            moTaCuChi = "Quay đầu sang bên trái",
            hanhDongCuChi = when (cheDo) {
                CheDoHuongDan.DIEU_HUONG ->
                    "Chuyển đến mục trước"

                CheDoHuongDan.MEDIA ->
                    "Chuyển nội dung trước"

                CheDoHuongDan.HO_TRO ->
                    "Chọn người liên hệ trước"

                CheDoHuongDan.CON_TRO ->
                    "Di chuyển con trỏ sang trái"
            },
            goiYCuChi =
                "Quay đầu nhẹ sang trái, không nghiêng vai",
            nhanCheDo =
                tenCheDo(cheDo),
            animationResId =
                R.raw.hd_quay_trai
        )
    }

    private fun taoQuayPhai(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 2,
            tenCuChi = "Quay phải",
            moTaCuChi = "Quay đầu sang bên phải",
            hanhDongCuChi = when (cheDo) {
                CheDoHuongDan.DIEU_HUONG ->
                    "Chuyển đến mục tiếp theo"

                CheDoHuongDan.MEDIA ->
                    "Chuyển nội dung tiếp theo"

                CheDoHuongDan.HO_TRO ->
                    "Chọn người liên hệ tiếp theo"

                CheDoHuongDan.CON_TRO ->
                    "Di chuyển con trỏ sang phải"
            },
            goiYCuChi =
                "Quay đầu nhẹ sang phải, không nghiêng vai",
            nhanCheDo =
                tenCheDo(cheDo),
            animationResId =
                R.raw.hd_quay_phai
        )
    }

    private fun taoNhinLen(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 3,
            tenCuChi = "Nhìn lên",
            moTaCuChi = "Ngẩng đầu nhẹ lên trên",
            hanhDongCuChi = when (cheDo) {
                CheDoHuongDan.DIEU_HUONG ->
                    "Cuộn xuống"

                CheDoHuongDan.MEDIA ->
                    "Tăng âm lượng"

                CheDoHuongDan.HO_TRO ->
                    "Xác nhận liên hệ"

                CheDoHuongDan.CON_TRO ->
                    "Di chuyển con trỏ lên"
            },
            goiYCuChi =
                "Ngẩng đầu tự nhiên, giữ mặt trong vùng camera",
            nhanCheDo =
                tenCheDo(cheDo),
            animationResId =
                R.raw.hd_nhin_len
        )
    }

    private fun taoNhinXuong(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 4,
            tenCuChi = "Nhìn xuống",
            moTaCuChi = "Cúi đầu nhẹ xuống dưới",
            hanhDongCuChi = when (cheDo) {
                CheDoHuongDan.DIEU_HUONG ->
                    "Cuộn lên"

                CheDoHuongDan.MEDIA ->
                    "Giảm âm lượng"

                CheDoHuongDan.HO_TRO ->
                    "Hủy liên hệ"

                CheDoHuongDan.CON_TRO ->
                    "Di chuyển con trỏ xuống"
            },
            goiYCuChi =
                "Cúi đầu nhẹ, không đưa toàn thân về phía trước",
            nhanCheDo =
                tenCheDo(cheDo),
            animationResId =
                R.raw.hd_nhin_xuong
        )
    }

    private fun taoNghiengTrai(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 5,
            tenCuChi = "Nghiêng trái",
            moTaCuChi = "Nghiêng đầu về phía vai trái",
            hanhDongCuChi =
                "Thực hiện chức năng đã chọn trong Cài đặt",
            goiYCuChi =
                "Nghiêng đầu khoảng 15–20°, không quay mặt sang trái",
            nhanCheDo =
                "TOÀN CỤC",
            animationResId =
                R.raw.hd_nghieng_trai
        )
    }

    private fun taoNghiengPhai(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 6,
            tenCuChi = "Nghiêng phải",
            moTaCuChi = "Nghiêng đầu về phía vai phải",
            hanhDongCuChi =
                "Thực hiện chức năng đã chọn trong Cài đặt",
            goiYCuChi =
                "Nghiêng đầu khoảng 15–20°, không quay mặt sang phải",
            nhanCheDo =
                "TOÀN CỤC",
            animationResId =
                R.raw.hd_nghieng_phai
        )
    }

    private fun taoNhamMat(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 7,
            tenCuChi = "Nhắm hai mắt",
            moTaCuChi = "Nhắm đồng thời cả hai mắt",
            hanhDongCuChi = when (cheDo) {
                CheDoHuongDan.DIEU_HUONG ->
                    "Xác nhận mục đang chọn"

                CheDoHuongDan.MEDIA ->
                    "Phát hoặc tạm dừng"

                CheDoHuongDan.HO_TRO ->
                    "Gọi hoặc kết thúc cuộc gọi"

                CheDoHuongDan.CON_TRO ->
                    "Nhấn tại vị trí con trỏ"
            },
            goiYCuChi =
                "Nhắm rõ cả hai mắt trong khoảng 0,5 giây",
            nhanCheDo =
                tenCheDo(cheDo),
            animationResId =
                R.raw.hd_nham_mat
        )
    }

    private fun taoMoMieng(
        cheDo: CheDoHuongDan
    ): MucHuongDanCuChi {

        return MucHuongDanCuChi(
            soThuTu = 8,
            tenCuChi = "Mở miệng",
            moTaCuChi = "Há miệng rõ ràng rồi khép lại",
            hanhDongCuChi =
                "Thực hiện chức năng đã chọn trong Cài đặt",
            goiYCuChi =
                "Há miệng rõ nhưng giữ đầu tương đối ổn định",
            nhanCheDo =
                "TOÀN CỤC",
            animationResId =
                R.raw.hd_mo_mieng
        )
    }

    private fun tenCheDo(
        cheDo: CheDoHuongDan
    ): String {

        return when (cheDo) {
            CheDoHuongDan.DIEU_HUONG ->
                "ĐIỀU HƯỚNG"

            CheDoHuongDan.MEDIA ->
                "MEDIA"

            CheDoHuongDan.HO_TRO ->
                "HỖ TRỢ"

            CheDoHuongDan.CON_TRO ->
                "CON TRỎ"
        }
    }
}