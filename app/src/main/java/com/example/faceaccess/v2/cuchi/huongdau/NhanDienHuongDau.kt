// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung

package com.example.faceaccess.v2.cuchi.huongdau

import com.example.faceaccess.v2.cuchi.cauhinh.CauHinhHuongDau
import kotlin.math.abs

class NhanDienHuongDau(
    private val cauHinh: CauHinhHuongDau = CauHinhHuongDau(),
    private val khiNhanDienHuong: (HuongDau) -> Unit
) {

    private enum class TrangThai {
        SAN_SANG,
        DANG_GIU,
        DA_KICH_HOAT,
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

    // Cho phép MediaPipe rớt khuôn mặt rất ngắn khi người dùng đang quay đầu.
    // Không coi một frame rỗng đơn lẻ là kết thúc phiên nhận diện.
    private var thoiDiemBatDauMatDuLieu =
        0L

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
            xuLyMatDuLieu(
                thoiGianMs = thoiGianMs
            )
            return
        }

        khoiPhucSauMatDuLieu(
            thoiGianMs = thoiGianMs
        )

        val dangTrungTinh =
            laTrungTinh(
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
                    dangTrungTinh = dangTrungTinh,
                    thoiGianMs = thoiGianMs
                )
            }

            TrangThai.CHO_TRUNG_TINH -> {
                capNhatChoTrungTinh(
                    dangTrungTinh = dangTrungTinh,
                    thoiGianMs = thoiGianMs
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

        val nguongYawCanThiet =
            if (yaw > 0f) {
                cauHinh.layNguongQuayTrai()
            } else {
                cauHinh.layNguongQuayPhai()
            }

        val yawChiPhoi =
            absYaw >= nguongYawCanThiet &&
                    absYaw >=
                    absPitch *
                    cauHinh.tyLeChiPhoiYaw &&
                    absYaw >=
                    absRoll *
                    cauHinh.tyLeChiPhoiYaw

        if (yawChiPhoi) {

            return if (yaw > 0f) {
                HuongDau.TRAI
            } else {
                HuongDau.PHAI
            }
        }

        val nguongPitchCanThiet =
            if (pitch > 0f) {
                cauHinh.layNguongNhinLen()
            } else {
                cauHinh.layNguongNhinXuong()
            }

        val pitchChiPhoi =
            absPitch >= nguongPitchCanThiet &&
                    absPitch >=
                    absYaw *
                    cauHinh.tyLeChiPhoiPitch &&
                    absPitch >=
                    absRoll *
                    cauHinh.tyLeChiPhoiPitch

        if (pitchChiPhoi) {

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
        yaw: Float,
        pitch: Float
    ): Boolean {

        // Vùng trung tính phải nhỏ hơn ngưỡng kích hoạt thực tế.
        // Nếu không, các mức độ nhạy cao (ngưỡng thấp) sẽ bị vùng
        // trung tính che mất và thanh độ nhạy không còn tác dụng.
        val nguongYawTrungTinhHieuLuc =
            minOf(
                cauHinh.nguongYawTrungTinh,
                minOf(
                    cauHinh.layNguongQuayTrai(),
                    cauHinh.layNguongQuayPhai()
                ) * TY_LE_TRUNG_TINH_SO_VOI_NGUONG
            )

        val nguongPitchTrungTinhHieuLuc =
            minOf(
                cauHinh.nguongPitchTrungTinh,
                minOf(
                    cauHinh.layNguongNhinLen(),
                    cauHinh.layNguongNhinXuong()
                ) * TY_LE_TRUNG_TINH_SO_VOI_NGUONG
            )

        // Detector YAW/PITCH chỉ cần hai trục của chính nó trở về giữa.
        // ROLL được detector nghiêng đầu xử lý riêng; dùng ROLL để khóa rearm
        // khiến người có tư thế đầu hơi nghiêng bị mất cử chỉ liên tiếp.
        return (
                abs(yaw) <=
                        nguongYawTrungTinhHieuLuc &&
                        abs(pitch) <=
                        nguongPitchTrungTinhHieuLuc
                )
    }

    private fun xuLyMatDuLieu(
        thoiGianMs: Long
    ) {

        if (thoiDiemBatDauMatDuLieu == 0L) {
            thoiDiemBatDauMatDuLieu =
                thoiGianMs
        }

        // Không cho thời gian trung tính chạy khi camera chưa thấy mặt.
        thoiDiemBatDauTrungTinh =
            0L

        val thoiGianMatDuLieu =
            thoiGianMs -
                    thoiDiemBatDauMatDuLieu

        if (
            thoiGianMatDuLieu >
            cauHinh.thoiGianGraceMs
        ) {
            chuyenSangChoTrungTinh()
        }
    }

    private fun khoiPhucSauMatDuLieu(
        thoiGianMs: Long
    ) {

        val batDauMatDuLieu =
            thoiDiemBatDauMatDuLieu

        if (batDauMatDuLieu == 0L) {
            return
        }

        val thoiGianMatDuLieu =
            thoiGianMs -
                    batDauMatDuLieu

        thoiDiemBatDauMatDuLieu =
            0L

        if (
            thoiGianMatDuLieu >
            cauHinh.thoiGianGraceMs
        ) {
            chuyenSangChoTrungTinh()
            return
        }

        if (trangThai == TrangThai.DANG_GIU) {
            // Không tính thời gian MediaPipe mất mặt vào thời gian giữ cử chỉ.
            thoiDiemBatDauGiu +=
                thoiGianMatDuLieu
        }
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

        thoiDiemBatDauMatDuLieu =
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

        thoiDiemBatDauMatDuLieu =
            0L
    }

    fun datLai() {
        chuyenSangChoTrungTinh()
    }

    companion object {
        private const val TY_LE_TRUNG_TINH_SO_VOI_NGUONG =
            0.85f
    }
}
