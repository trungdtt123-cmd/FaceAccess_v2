package com.example.faceaccess.v2.chedo

class BoDinhTuyenCheDo(
    private val khiCheDoThayDoi:
        (CheDoDieuKhien) -> Unit
) {

    private var cheDoHienTai:
            CheDoDieuKhien =
        CheDoDieuKhien.DIEU_HUONG

    /**
     * Trả về chế độ hiện tại.
     */
    fun layCheDoHienTai():
            CheDoDieuKhien {

        return cheDoHienTai
    }

    /**
     * Chuyển sang chế độ kế tiếp.
     *
     * DIEU_HUONG
     * → MEDIA
     * → HO_TRO
     * → CON_TRO
     * → DIEU_HUONG
     */
    fun chuyenCheDoTiepTheo() {

        cheDoHienTai =
            when (cheDoHienTai) {

                CheDoDieuKhien.DIEU_HUONG ->
                    CheDoDieuKhien.MEDIA

                CheDoDieuKhien.MEDIA ->
                    CheDoDieuKhien.HO_TRO

                CheDoDieuKhien.HO_TRO ->
                    CheDoDieuKhien.CON_TRO

                CheDoDieuKhien.CON_TRO ->
                    CheDoDieuKhien.DIEU_HUONG
            }

        khiCheDoThayDoi(
            cheDoHienTai
        )
    }

    /**
     * Đặt lại về Điều hướng.
     *
     * Hiện chưa sử dụng nhưng giữ API rõ ràng
     * cho lifecycle/session sau này.
     */
    fun datLai() {

        cheDoHienTai =
            CheDoDieuKhien.DIEU_HUONG

        khiCheDoThayDoi(
            cheDoHienTai
        )
    }
}