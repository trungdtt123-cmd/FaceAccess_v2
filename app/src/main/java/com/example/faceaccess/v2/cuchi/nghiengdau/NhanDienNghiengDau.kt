package com.example.faceaccess.v2.cuchi.nghiengdau

import kotlin.math.abs

class NhanDienNghiengDau(
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

    /**
     * Gọi hàm này mỗi khi có dữ liệu khuôn mặt mới.
     */
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
                    THOI_GIAN_GIU_MS
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
                    THOI_GIAN_GIU_MS
                ) {

                    khiNhanDien(
                        HuongNghiengDau.PHAI
                    )

                    trangThai =
                        TrangThai.DA_KICH_HOAT
                }
            }

            TrangThai.DA_KICH_HOAT -> {

                /*
                 * Sau khi đã nhận một gesture,
                 * phải đưa đầu về gần trung tâm.
                 *
                 * Không cho giữ đầu nghiêng và
                 * kích hoạt liên tục.
                 */
                if (laTrungTinh(roll)) {

                    trangThai =
                        TrangThai.SAN_SANG
                }
            }
        }
    }

    /**
     * Tránh nhầm quay đầu YAW thành nghiêng ROLL.
     */
    private fun laNghiengTrai(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        if (roll > NGUONG_TRAI) {
            return false
        }

        return rollChiPhoi(
            roll,
            yaw,
            pitch
        )
    }

    private fun laNghiengPhai(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        if (roll < NGUONG_PHAI) {
            return false
        }

        return rollChiPhoi(
            roll,
            yaw,
            pitch
        )
    }

    /**
     * ROLL phải là chuyển động chính.
     *
     * Ta không yêu cầu tuyệt đối quá chặt
     * vì khi người thật nghiêng đầu,
     * YAW/PITCH vẫn có thể thay đổi nhẹ.
     */
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
                        yawAbs * TY_LE_CHI_PHOI
                        &&
                        rollAbs >=
                        pitchAbs * TY_LE_CHI_PHOI
                )
    }

    private fun laTrungTinh(
        roll: Float
    ): Boolean {

        return abs(roll) <=
                NGUONG_TRUNG_TINH
    }

    /**
     * Dùng khi camera dừng hoặc mất session.
     */
    fun datLai() {

        trangThai =
            TrangThai.SAN_SANG

        thoiGianBatDauGiu =
            0L
    }

    companion object {

        /*
         * Dữ liệu thực tế:
         *
         * trái thường khoảng <= -20
         * phải thường khoảng >= +20
         *
         * Ta dùng +/-16 để thao tác
         * tự nhiên hơn và ít phải gồng.
         */

        private const val NGUONG_TRAI =
            -16f

        private const val NGUONG_PHAI =
            16f

        /*
         * Phải về gần trung tâm mới re-arm.
         */
        private const val NGUONG_TRUNG_TINH =
            7f

        /*
         * 260ms đủ để phân biệt gesture
         * có chủ đích với rung/chuyển động nhanh.
         */
        private const val THOI_GIAN_GIU_MS =
            260L

        /*
         * Không quá nghiêm để người dùng
         * có thể nghiêng tự nhiên.
         */
        private const val TY_LE_CHI_PHOI =
            0.75f
    }
}