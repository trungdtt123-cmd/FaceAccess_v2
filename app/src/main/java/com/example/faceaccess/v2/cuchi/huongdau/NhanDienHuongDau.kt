package com.example.faceaccess.v2.cuchi.huongdau

import kotlin.math.abs

class NhanDienHuongDau(
    private val khiNhanDienHuong:
        (HuongDau) -> Unit
) {

    // =========================================================
    // TRẠNG THÁI
    // =========================================================

    private enum class TrangThai {

        /**
         * Đang ở tư thế trung tính và sẵn sàng
         * nhận một cử chỉ mới.
         */
        SAN_SANG,

        /**
         * Đã phát hiện một hướng hợp lệ và
         * đang chờ người dùng giữ đủ thời gian.
         */
        DANG_GIU,

        /**
         * Đã phát event đúng một lần.
         * Phải quay về trung tính mới re-arm.
         */
        DA_KICH_HOAT,

        /**
         * Candidate bị đổi hướng / mất điều kiện
         * khi đầu vẫn chưa về trung tính.
         *
         * Không cho nhận hướng mới ngay lập tức.
         */
        CHO_TRUNG_TINH
    }


    private var trangThai =
        TrangThai.SAN_SANG

    private var huongDangGiu:
            HuongDau? = null

    private var thoiDiemBatDauGiu =
        0L

    private var thoiDiemHopLeCuoi =
        0L

    private var thoiDiemBatDauTrungTinh =
        0L


    // =========================================================
    // CẬP NHẬT
    // =========================================================

    fun capNhat(
        roll: Float?,
        yaw: Float?,
        pitch: Float?,
        thoiGianMs: Long
    ) {

        /*
         * Thiếu pose thì reset toàn bộ.
         * Không giữ candidate cũ khi tracking mất.
         */
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

            // =================================================
            // SẴN SÀNG
            // =================================================

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


            // =================================================
            // ĐANG GIỮ
            // =================================================

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


                /*
                 * Hướng thay đổi giữa chừng:
                 *
                 * Ví dụ candidate TRAI rồi chuyển sang LEN
                 * khi chưa về trung tính.
                 *
                 * Không cho "đổi gesture giữa đường".
                 */
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


                    if (
                        thoiGianDaGiu >=
                        THOI_GIAN_GIU_MS
                    ) {

                        val huongPhat =
                            huongDangGiu
                                ?: return


                        /*
                         * One-shot:
                         * phát event đúng một lần.
                         */
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


                /*
                 * Candidate có thể mất trong vài frame do
                 * nhiễu MediaPipe.
                 *
                 * Cho một khoảng grace nhỏ thay vì reset ngay.
                 */
                val thoiGianMatDieuKien =
                    thoiGianMs -
                            thoiDiemHopLeCuoi


                if (
                    thoiGianMatDieuKien >
                    THOI_GIAN_GRACE_MS
                ) {

                    chuyenSangChoTrungTinh()
                }
            }


            // =================================================
            // ĐÃ KÍCH HOẠT
            // =================================================

            TrangThai.DA_KICH_HOAT -> {

                capNhatChoTrungTinh(
                    dangTrungTinh =
                        dangTrungTinh,
                    thoiGianMs =
                        thoiGianMs
                )
            }


            // =================================================
            // CHỜ TRUNG TÍNH
            // =================================================

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


    // =========================================================
    // XÁC ĐỊNH HƯỚNG
    // =========================================================

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


        /*
         * YAW phải thắng cả PITCH và ROLL.
         *
         * Điều này giúp xoay trái/phải không "ăn" vào
         * detector ROLL nghiêng đầu đang có.
         */
        val yawChiPhoi =
            absYaw >= NGUONG_YAW &&
                    absYaw >=
                    absPitch * TY_LE_CHI_PHOI &&
                    absYaw >=
                    absRoll * TY_LE_CHI_PHOI


        if (yawChiPhoi) {

            /*
             * Dấu đã được xác nhận từ pipeline hiện tại:
             *
             * physical LEFT  -> yaw dương
             * physical RIGHT -> yaw âm
             */
            return if (yaw > 0f) {

                HuongDau.TRAI

            } else {

                HuongDau.PHAI
            }
        }


        /*
         * PITCH phải thắng cả YAW và ROLL.
         */
        val pitchChiPhoi =
            absPitch >= NGUONG_PITCH &&
                    absPitch >=
                    absYaw * TY_LE_CHI_PHOI &&
                    absPitch >=
                    absRoll * TY_LE_CHI_PHOI


        if (pitchChiPhoi) {

            /*
             * Dấu đã được xác nhận từ pipeline hiện tại:
             *
             * nhìn lên   -> pitch dương
             * nhìn xuống -> pitch âm
             */
            return if (pitch > 0f) {

                HuongDau.LEN

            } else {

                HuongDau.XUONG
            }
        }


        return null
    }


    // =========================================================
    // TRUNG TÍNH / RE-ARM
    // =========================================================

    private fun laTrungTinh(
        roll: Float,
        yaw: Float,
        pitch: Float
    ): Boolean {

        return (
                abs(roll) <=
                        NGUONG_ROLL_TRUNG_TINH &&
                        abs(yaw) <=
                        NGUONG_YAW_TRUNG_TINH &&
                        abs(pitch) <=
                        NGUONG_PITCH_TRUNG_TINH
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
            THOI_GIAN_TRUNG_TINH_MS
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


    // =========================================================
    // RESET PUBLIC
    // =========================================================

    fun datLai() {

        datLaiVeSanSang()
    }


    // =========================================================
    // NGƯỠNG
    // =========================================================

    companion object {

        /**
         * YAW trái/phải.
         *
         * Dữ liệu runtime trước đó cho thấy xoay rõ
         * thường vượt khoảng 30 độ.
         *
         * Dùng 19 độ để thao tác tự nhiên hơn nhưng vẫn có
         * khoảng cách với chuyển động đầu nhỏ.
         */
        private const val NGUONG_YAW =
            19f

        /**
         * PITCH thường có biên độ tự nhiên nhỏ hơn YAW,
         * vì vậy dùng ngưỡng thấp hơn một chút.
         */
        private const val NGUONG_PITCH =
            14f

        /**
         * Một trục chỉ được nhận nếu nó thực sự chi phối
         * hai trục còn lại.
         */
        private const val TY_LE_CHI_PHOI =
            1.10f

        /**
         * Neutral window để re-arm.
         */
        private const val NGUONG_ROLL_TRUNG_TINH =
            8f

        private const val NGUONG_YAW_TRUNG_TINH =
            11f

        private const val NGUONG_PITCH_TRUNG_TINH =
            9f

        /**
         * Người dùng phải giữ hướng đủ lâu để coi là
         * cử chỉ chủ ý, không phải chuyển động thoáng qua.
         */
        private const val THOI_GIAN_GIU_MS =
            180L

        /**
         * Cho phép một vài frame nhiễu trong lúc giữ.
         */
        private const val THOI_GIAN_GRACE_MS =
            220L

        /**
         * Phải trở lại neutral ổn định trước gesture mới.
         */
        private const val THOI_GIAN_TRUNG_TINH_MS =
            100L
    }
}