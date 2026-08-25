package com.example.faceaccess.v2.chedo

/**
 * Nguồn trạng thái chế độ duy nhất của FaceAccess.
 *
 * Cả Activity và Foreground Service đều phải đọc/ghi
 * chế độ thông qua object này.
 *
 * Nhờ đó không xảy ra tình trạng:
 *
 * Activity = MEDIA
 * Service  = DIEU_HUONG
 */
object TrangThaiCheDoToanCuc {

    private val khoa =
        Any()

    @Volatile
    private var cheDoHienTai:
            CheDoDieuKhien =
        CheDoDieuKhien.DIEU_HUONG


    /**
     * Lấy chế độ hiện tại.
     */
    fun layCheDoHienTai():
            CheDoDieuKhien {

        return cheDoHienTai
    }


    /**
     * Chuyển sang chế độ tiếp theo.
     *
     * DIEU_HUONG
     * -> MEDIA
     * -> HO_TRO
     * -> CON_TRO
     * -> DIEU_HUONG
     *
     * synchronized để tránh Activity và Service
     * thay đổi state cùng thời điểm.
     */
    fun chuyenCheDoTiepTheo():
            CheDoDieuKhien {

        synchronized(khoa) {

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

            return cheDoHienTai
        }
    }


    /**
     * Đặt lại hệ thống về Điều hướng.
     */
    fun datLai():
            CheDoDieuKhien {

        synchronized(khoa) {

            cheDoHienTai =
                CheDoDieuKhien.DIEU_HUONG

            return cheDoHienTai
        }
    }
}