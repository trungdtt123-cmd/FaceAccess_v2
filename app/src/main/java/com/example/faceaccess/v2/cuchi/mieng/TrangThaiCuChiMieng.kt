package com.example.faceaccess.v2.cuchi.mieng

import android.os.SystemClock

object TrangThaiCuChiMieng {

    @Volatile
    private var dangXuLyCuChiMieng =
        false

    @Volatile
    private var thoiDiemBoChanHuongDauMs =
        0L

    fun batDau() {
        dangXuLyCuChiMieng =
            true

        thoiDiemBoChanHuongDauMs =
            0L
    }

    fun ketThucVoiGuard() {
        dangXuLyCuChiMieng =
            false

        thoiDiemBoChanHuongDauMs =
            SystemClock.elapsedRealtime() +
                    THOI_GIAN_GUARD_MS
    }

    fun huy() {
        dangXuLyCuChiMieng =
            false

        thoiDiemBoChanHuongDauMs =
            0L
    }

    fun dangChanHuongDau(): Boolean {
        if (dangXuLyCuChiMieng) {
            return true
        }

        return SystemClock.elapsedRealtime() <
                thoiDiemBoChanHuongDauMs
    }

    private const val THOI_GIAN_GUARD_MS =
        180L
}