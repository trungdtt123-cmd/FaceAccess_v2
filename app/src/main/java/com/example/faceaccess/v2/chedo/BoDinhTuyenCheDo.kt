package com.example.faceaccess.v2.chedo

/**
 * Tầng điều khiển việc chuyển chế độ.
 *
 * Class này KHÔNG tự giữ state nữa.
 *
 * State thật nằm duy nhất tại:
 * TrangThaiCheDoToanCuc
 */
class BoDinhTuyenCheDo(
    private val khiCheDoThayDoi:
        (CheDoDieuKhien) -> Unit
) {

    /**
     * Trả về chế độ hiện tại từ
     * nguồn trạng thái toàn cục.
     */
    fun layCheDoHienTai():
            CheDoDieuKhien {

        return TrangThaiCheDoToanCuc
            .layCheDoHienTai()
    }


    /**
     * Chuyển sang chế độ kế tiếp.
     *
     * Việc thay đổi state thật được thực hiện
     * trong TrangThaiCheDoToanCuc.
     */
    fun chuyenCheDoTiepTheo() {

        val cheDoMoi =
            TrangThaiCheDoToanCuc
                .chuyenCheDoTiepTheo()

        khiCheDoThayDoi(
            cheDoMoi
        )
    }


    /**
     * Đặt lại về Điều hướng.
     */
    fun datLai() {

        val cheDoMoi =
            TrangThaiCheDoToanCuc
                .datLai()

        khiCheDoThayDoi(
            cheDoMoi
        )
    }
}