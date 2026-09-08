package com.example.faceaccess.v2.cuchi.nghiengdau

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhNghiengDau
import kotlin.math.abs

class NhanDienNghiengDau(

    // Cấu hình nhận diện nghiêng đầu
    private val cauHinh: CauHinhNghiengDau =
        CauHinhNghiengDau(),

    // Callback khi nhận diện thành công
    private val khiNhanDien:
        (HuongNghiengDau) -> Unit

) {

    private enum class TrangThai {
        SAN_SANG,
        DANG_GIU_TRAI,
        DANG_GIU_PHAI,
        DA_KICH_HOAT
    }

    private var trangThai =
        TrangThai.SAN_SANG

    private var thoiGianBatDauGiu =
        0L


    // Cập nhật dữ liệu khuôn mặt mới
    fun capNhat(
        roll: Float?,
        yaw: Float?,
        pitch: Float?,
        thoiGianMs: Long
    ) {

        if (
            roll == null ||
            yaw == null ||
            pitch == null
        ) {
            return
        }

        when (trangThai) {

            TrangThai.SAN_SANG -> {

                when {

                    laNghiengTrai(
                        roll,
                        yaw,
                        pitch
                    ) -> {

                        trangThai =
                            TrangThai.DANG_GIU_TRAI

                        thoiGianBatDauGiu =
                            thoiGianMs
                    }

                    laNghiengPhai(
                        roll,
                        yaw,
                        pitch
                    ) -> {

                        trangThai =
                            TrangThai.DANG_GIU_PHAI

                        thoiGianBatDauGiu =
                            thoiGianMs
                    }
                }
            }

            TrangThai.DANG_GIU_TRAI -> {

                if (
                    !laNghiengTrai(
                        roll,
                        yaw,
                        pitch
                    )
                ) {

                    trangThai =
                        TrangThai.SAN_SANG

                    return
                }

                if (
                    thoiGianMs -
                    thoiGianBatDauGiu >=
                    cauHinh.thoiGianGiuMs
                ) {

                    khiNhanDien(
                        HuongNghiengDau.TRAI
                    )

                    trangThai =
                        TrangThai.DA_KICH_HOAT
                }
            }

            TrangThai.DANG_GIU_PHAI -> {

                if (
                    !laNghiengPhai(
                        roll,
                        yaw,
                        pitch
                    )
                ) {

                    trangThai =
                        TrangThai.SAN_SANG

                    return
                }

                if (
                    thoiGianMs -
                    thoiGianBatDauGiu >=
                    cauHinh.thoiGianGiuMs
                ) {

                    khiNhanDien(
                        HuongNghiengDau.PHAI
                    )

                    trangThai =
                        TrangThai.DA_KICH_HOAT
                }
            }

            TrangThai.DA_KICH_HOAT -> {

                // Phải về trung tính mới nhận cử chỉ tiếp theo
                if (laTrungTinh(roll)) {

                    trangThai =
                        TrangThai.SAN_SANG
                }
            }
        }
    }


    // Kiểm tra nghiêng đầu sang trái
    private fun laNghiengTrai(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        if (roll > cauHinh.nguongTrai) {
            return false
        }

        return rollChiPhoi(
            roll,
            yaw,
            pitch
        )
    }


    // Kiểm tra nghiêng đầu sang phải
    private fun laNghiengPhai(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        if (roll < cauHinh.nguongPhai) {
            return false
        }

        return rollChiPhoi(
            roll,
            yaw,
            pitch
        )
    }


    // Roll phải là chuyển động chính
    private fun rollChiPhoi(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        val rollAbs =
            abs(roll)

        val yawAbs =
            abs(yaw)

        val pitchAbs =
            abs(pitch)

        return (
                rollAbs >=
                        yawAbs *
                        cauHinh.tyLeChiPhoi
                        &&
                        rollAbs >=
                        pitchAbs *
                        cauHinh.tyLeChiPhoi
                )
    }


    // Kiểm tra đầu đã trở về trung tính
    private fun laTrungTinh(
        roll: Float
    ): Boolean {

        return abs(roll) <=
                cauHinh.nguongTrungTinh
    }


    // Reset khi camera dừng hoặc mất session
    fun datLai() {

        trangThai =
            TrangThai.SAN_SANG

        thoiGianBatDauGiu =
            0L
    }
}