package com.example.faceaccess.v2.cuchi.mieng

class NhanDienMoMieng(
    private val khiNhanDienMoMieng: () -> Unit
) {

    // =========================================================
    // TRẠNG THÁI
    // =========================================================

    private enum class TrangThai {

        /**
         * Miệng đang đóng.
         * Detector sẵn sàng nhận một lần mở mới.
         */
        SAN_SANG,

        /**
         * Miệng đã vượt ngưỡng mở nhưng chưa đủ
         * thời gian để xác nhận là cử chỉ chủ ý.
         */
        DANG_MO,

        /**
         * Đã phát event một lần.
         *
         * Phải đóng miệng trở lại ổn định
         * mới được phép nhận lần tiếp theo.
         */
        DA_KICH_HOAT
    }


    private var trangThai =
        TrangThai.SAN_SANG

    private var thoiDiemBatDauMo =
        0L

    private var thoiDiemBatDauDong =
        0L


    // =========================================================
    // CẬP NHẬT
    // =========================================================

    fun capNhat(
        doMoMieng: Float?,
        thoiGianMs: Long
    ) {

        /*
         * Không có dữ liệu thì reset.
         *
         * Không giữ gesture cũ khi tracking mất.
         */
        if (doMoMieng == null) {

            datLai()

            return
        }


        when (trangThai) {

            // =================================================
            // SẴN SÀNG
            // =================================================

            TrangThai.SAN_SANG -> {

                if (
                    doMoMieng >=
                    NGUONG_MO
                ) {

                    thoiDiemBatDauMo =
                        thoiGianMs

                    trangThai =
                        TrangThai.DANG_MO
                }
            }


            // =================================================
            // ĐANG MỞ
            // =================================================

            TrangThai.DANG_MO -> {

                if (
                    doMoMieng >=
                    NGUONG_MO
                ) {

                    val thoiGianDaMo =
                        thoiGianMs -
                                thoiDiemBatDauMo

                    if (
                        thoiGianDaMo >=
                        THOI_GIAN_GIU_MO_MS
                    ) {

                        /*
                         * Chỉ phát event đúng một lần.
                         */
                        khiNhanDienMoMieng()

                        trangThai =
                            TrangThai.DA_KICH_HOAT

                        thoiDiemBatDauDong =
                            0L
                    }

                } else {

                    /*
                     * Chưa giữ đủ lâu mà đã đóng lại
                     * thì hủy lần nhận diện này.
                     */
                    trangThai =
                        TrangThai.SAN_SANG

                    thoiDiemBatDauMo =
                        0L
                }
            }


            // =================================================
            // ĐÃ KÍCH HOẠT
            // =================================================

            TrangThai.DA_KICH_HOAT -> {

                if (
                    doMoMieng <=
                    NGUONG_DONG
                ) {

                    if (
                        thoiDiemBatDauDong == 0L
                    ) {

                        thoiDiemBatDauDong =
                            thoiGianMs
                    }


                    val thoiGianDaDong =
                        thoiGianMs -
                                thoiDiemBatDauDong


                    if (
                        thoiGianDaDong >=
                        THOI_GIAN_DONG_ON_DINH_MS
                    ) {

                        /*
                         * Đã đóng miệng ổn định.
                         *
                         * Detector được re-arm cho
                         * lần mở tiếp theo.
                         */
                        trangThai =
                            TrangThai.SAN_SANG

                        thoiDiemBatDauMo =
                            0L

                        thoiDiemBatDauDong =
                            0L
                    }

                } else {

                    /*
                     * Miệng chưa thực sự đóng trở lại.
                     */
                    thoiDiemBatDauDong =
                        0L
                }
            }
        }
    }


    // =========================================================
    // RESET
    // =========================================================

    fun datLai() {

        trangThai =
            TrangThai.SAN_SANG

        thoiDiemBatDauMo =
            0L

        thoiDiemBatDauDong =
            0L
    }


    // =========================================================
    // NGƯỠNG
    // =========================================================

    companion object {

        /**
         * Dữ liệu bạn đã đo:
         *
         * miệng đóng ≈ 0.013
         * miệng mở  >= 0.400
         *
         * 0.35 tạo khoảng cách khá rõ với trạng thái đóng.
         */
        private const val NGUONG_MO =
            0.35f

        /**
         * Hysteresis:
         *
         * mở >= 0.35
         * đóng <= 0.10
         *
         * tránh rung quanh một ngưỡng duy nhất.
         */
        private const val NGUONG_DONG =
            0.10f

        /**
         * Không kích hoạt ngay khi score vừa vượt ngưỡng.
         * Người dùng phải mở miệng có chủ ý trong khoảng
         * thời gian ngắn.
         */
        private const val THOI_GIAN_GIU_MO_MS =
            220L

        /**
         * Sau khi kích hoạt, cần đóng miệng ổn định
         * trước khi detector được phép nhận lần tiếp theo.
         */
        private const val THOI_GIAN_DONG_ON_DINH_MS =
            150L
    }
}