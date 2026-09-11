// SPDX-License-Identifier: MIT
// Copyright (c) 2026 Hoàng Thị Kiều Anh, Phạm Văn Dượng, Đặng Quốc Trung
package com.example.faceaccess.v2.cuchi.mieng

import android.os.SystemClock

object TrangThaiCuChiMieng {

    @Volatile
    private var dangXuLyCuChiMieng =
        false

    @Volatile
    private var thoiDiemBoChanHuongDauMs =
        0L

    @Volatile
    private var dangUuTienMoHaiLan =
        false

    fun batDau() {
        dangXuLyCuChiMieng =
            true

        thoiDiemBoChanHuongDauMs =
            0L
    }

    fun batDauUuTienMoHaiLan() {
        dangUuTienMoHaiLan =
            true
    }

    fun ketThucVoiGuard() {
        dangXuLyCuChiMieng =
            false

        dangUuTienMoHaiLan =
            false

        thoiDiemBoChanHuongDauMs =
            SystemClock.elapsedRealtime() +
                    THOI_GIAN_GUARD_MS
    }

    fun huy() {
        dangXuLyCuChiMieng =
            false

        dangUuTienMoHaiLan =
            false

        thoiDiemBoChanHuongDauMs =
            0L
    }

    fun dangChanMoGiu(): Boolean {
        return dangUuTienMoHaiLan
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