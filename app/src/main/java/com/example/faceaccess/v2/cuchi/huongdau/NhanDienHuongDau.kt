package com.example.faceaccess.v2.cuchi.huongdau

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhHuongDau
import kotlin.math.abs

class NhanDienHuongDau(

    // Cấu hình nhận diện hướng đầu
    private val cauHinh: CauHinhHuongDau =
        CauHinhHuongDau(),

    // Callback khi nhận diện thành công
    private val khiNhanDienHuong:
        (HuongDau) -> Unit

) {

    private enum class TrangThai {

        // Sẵn sàng nhận cử chỉ
        SAN_SANG,

        // Đang giữ một hướng
        DANG_GIU,

        // Đã phát cử chỉ
        DA_KICH_HOAT,

        // Chờ đầu về trung tính
        CHO_TRUNG_TINH
    }


    private var trangThai =
        TrangThai.CHO_TRUNG_TINH

    private var huongDangGiu:
            HuongDau? = null

    private var thoiDiemBatDauGiu =
        0L

    private var thoiDiemHopLeCuoi =
        0L

    private var thoiDiemBatDauTrungTinh =
        0L


    fun capNhat(
        roll: Float?,
        yaw: Float?,
        pitch: Float?,
        thoiGianMs: Long
    ) {

        // Thiếu pose thì reset
        if (
            roll == null ||
            yaw == null ||
            pitch == null
        ) {

            datLai()

            return
        }


        val dangTrungTinh =
            laTrungTinh(
                roll = roll,
                yaw = yaw,
                pitch = pitch
            )


        when (trangThai) {

            TrangThai.SAN_SANG -> {

                if (dangTrungTinh) {

                    thoiDiemBatDauTrungTinh =
                        0L

                    return
                }


                val huong =
                    timHuongHopLe(
                        roll = roll,
                        yaw = yaw,
                        pitch = pitch
                    )


                if (huong != null) {

                    huongDangGiu =
                        huong

                    thoiDiemBatDauGiu =
                        thoiGianMs

                    thoiDiemHopLeCuoi =
                        thoiGianMs

                    trangThai =
                        TrangThai.DANG_GIU
                }
            }


            TrangThai.DANG_GIU -> {

                if (dangTrungTinh) {

                    datLaiVeSanSang()

                    return
                }


                val huongHienTai =
                    timHuongHopLe(
                        roll = roll,
                        yaw = yaw,
                        pitch = pitch
                    )


                // Không đổi cử chỉ giữa chừng
                if (
                    huongHienTai != null &&
                    huongHienTai != huongDangGiu
                ) {

                    chuyenSangChoTrungTinh()

                    return
                }


                if (
                    huongHienTai ==
                    huongDangGiu
                ) {

                    thoiDiemHopLeCuoi =
                        thoiGianMs


                    val thoiGianDaGiu =
                        thoiGianMs -
                                thoiDiemBatDauGiu


                    val thoiGianGiuCanThiet =
                        layThoiGianGiuCanThiet(
                            huongDangGiu
                        )


                    if (
                        thoiGianDaGiu >=
                        thoiGianGiuCanThiet
                    ) {

                        val huongPhat =
                            huongDangGiu
                                ?: return


                        // Chỉ phát cử chỉ một lần
                        khiNhanDienHuong(
                            huongPhat
                        )


                        trangThai =
                            TrangThai.DA_KICH_HOAT

                        thoiDiemBatDauTrungTinh =
                            0L
                    }

                    return
                }


                // Cho phép nhiễu MediaPipe ngắn
                val thoiGianMatDieuKien =
                    thoiGianMs -
                            thoiDiemHopLeCuoi


                if (
                    thoiGianMatDieuKien >
                    cauHinh.thoiGianGraceMs
                ) {

                    chuyenSangChoTrungTinh()
                }
            }


            TrangThai.DA_KICH_HOAT -> {

                capNhatChoTrungTinh(
                    dangTrungTinh =
                        dangTrungTinh,
                    thoiGianMs =
                        thoiGianMs
                )
            }


            TrangThai.CHO_TRUNG_TINH -> {

                capNhatChoTrungTinh(
                    dangTrungTinh =
                        dangTrungTinh,
                    thoiGianMs =
                        thoiGianMs
                )
            }
        }
    }


    private fun timHuongHopLe(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): HuongDau? {

        val absRoll =
            abs(roll)

        val absYaw =
            abs(yaw)

        val absPitch =
            abs(pitch)


        // Yaw phải chi phối pitch và roll
        val yawChiPhoi =
            absYaw >=
                    cauHinh.nguongYaw &&
                    absYaw >=
                    absPitch *
                    cauHinh.tyLeChiPhoiYaw &&
                    absYaw >=
                    absRoll *
                    cauHinh.tyLeChiPhoiYaw


        if (yawChiPhoi) {

            // Yaw dương: trái, yaw âm: phải
            return if (yaw > 0f) {

                HuongDau.TRAI

            } else {

                HuongDau.PHAI
            }
        }


        // Pitch phải chi phối yaw và roll
        val pitchChiPhoi =
            absPitch >=
                    cauHinh.nguongPitch &&
                    absPitch >=
                    absYaw *
                    cauHinh.tyLeChiPhoiPitch &&
                    absPitch >=
                    absRoll *
                    cauHinh.tyLeChiPhoiPitch


        if (pitchChiPhoi) {

            // Pitch dương: lên, pitch âm: xuống
            return if (pitch > 0f) {

                HuongDau.LEN

            } else {

                HuongDau.XUONG
            }
        }


        return null
    }


    private fun layThoiGianGiuCanThiet(
        huong: HuongDau?
    ): Long {

        return when (huong) {

            HuongDau.LEN,
            HuongDau.XUONG ->
                cauHinh.thoiGianGiuPitchMs

            HuongDau.TRAI,
            HuongDau.PHAI ->
                cauHinh.thoiGianGiuYawMs

            null ->
                cauHinh.thoiGianGiuYawMs
        }
    }


    private fun laTrungTinh(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        return (
                abs(roll) <=
                        cauHinh.nguongRollTrungTinh &&
                        abs(yaw) <=
                        cauHinh.nguongYawTrungTinh &&
                        abs(pitch) <=
                        cauHinh.nguongPitchTrungTinh
                )
    }


    private fun capNhatChoTrungTinh(
        dangTrungTinh: Boolean,
        thoiGianMs: Long
    ) {

        if (!dangTrungTinh) {

            thoiDiemBatDauTrungTinh =
                0L

            return
        }


        if (
            thoiDiemBatDauTrungTinh ==
            0L
        ) {

            thoiDiemBatDauTrungTinh =
                thoiGianMs

            return
        }


        val thoiGianDaTrungTinh =
            thoiGianMs -
                    thoiDiemBatDauTrungTinh


        if (
            thoiGianDaTrungTinh >=
            cauHinh.thoiGianTrungTinhMs
        ) {

            datLaiVeSanSang()
        }
    }


    private fun chuyenSangChoTrungTinh() {

        trangThai =
            TrangThai.CHO_TRUNG_TINH

        huongDangGiu =
            null

        thoiDiemBatDauGiu =
            0L

        thoiDiemHopLeCuoi =
            0L

        thoiDiemBatDauTrungTinh =
            0L
    }


    private fun datLaiVeSanSang() {

        trangThai =
            TrangThai.SAN_SANG

        huongDangGiu =
            null

        thoiDiemBatDauGiu =
            0L

        thoiDiemHopLeCuoi =
            0L

        thoiDiemBatDauTrungTinh =
            0L
    }


    fun datLai() {

        // Chờ trung tính trước khi nhận lại
        chuyenSangChoTrungTinh()
    }
}