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
        TrangThai.CHO_TRUNG_TINH

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
                    absPitch * TY_LE_CHI_PHOI_YAW &&
                    absYaw >=
                    absRoll * TY_LE_CHI_PHOI_YAW


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
                    absYaw * TY_LE_CHI_PHOI_PITCH &&
                    absPitch >=
                    absRoll * TY_LE_CHI_PHOI_PITCH


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
    // THỜI GIAN GIỮ THEO HƯỚNG
    // =========================================================

    private fun layThoiGianGiuCanThiet(
        huong: HuongDau?
    ): Long {

        return when (huong) {

            HuongDau.LEN,
            HuongDau.XUONG ->
                THOI_GIAN_GIU_PITCH_MS

            HuongDau.TRAI,
            HuongDau.PHAI ->
                THOI_GIAN_GIU_YAW_MS

            null ->
                THOI_GIAN_GIU_YAW_MS
        }
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

        /*
         * Sau camera handoff / tracking loss không cho nhận
         * gesture ngay từ frame đầu tiên vì head pose có thể
         * còn dao động.
         *
         * Yêu cầu một khoảng neutral rất ngắn trước khi re-arm.
         */
        chuyenSangChoTrungTinh()
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
         * PITCH có biên độ tự nhiên nhỏ hơn YAW.
         *
         * Giảm từ 14 xuống 11 độ để ngẩng/cúi nhẹ
         * cũng được nhận tự nhiên hơn.
         */
        private const val NGUONG_PITCH =
            11f

        /**
         * Giữ YAW chặt hơn để không ăn vào ROLL.
         */
        private const val TY_LE_CHI_PHOI_YAW =
            1.10f

        /**
         * PITCH được nới nhẹ dominance để thao tác
         * ngẩng/cúi không cần quá "thẳng trục".
         */
        private const val TY_LE_CHI_PHOI_PITCH =
            1.05f

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
         * YAW đã khá ổn nên chỉ giảm hold vừa phải
         * để phản hồi nhanh hơn mà không quá nhạy.
         */
        private const val THOI_GIAN_GIU_YAW_MS =
            140L

        /**
         * PITCH cần cảm giác nhanh và tự nhiên hơn.
         */
        private const val THOI_GIAN_GIU_PITCH_MS =
            120L

        /**
         * Vẫn giữ grace đủ lớn để không mất candidate
         * chỉ vì 1-2 frame MediaPipe nhiễu.
         */
        private const val THOI_GIAN_GRACE_MS =
            200L

        /**
         * Neutral ổn định ngắn hơn để re-arm nhanh,
         * đồng thời giúp startup/handoff sẵn sàng sớm.
         */
        private const val THOI_GIAN_TRUNG_TINH_MS =
            80L
    }
}