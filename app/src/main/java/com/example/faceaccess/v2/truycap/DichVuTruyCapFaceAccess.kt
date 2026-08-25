package com.example.faceaccess.v2.truycap

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class DichVuTruyCapFaceAccess : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()

        phienBanDangHoatDong = this

        Log.d(
            TAG,
            "Dich vu truy cap da ket noi"
        )
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        /*
         * Hiện tại FaceAccess chưa cần đọc
         * AccessibilityEvent.
         *
         * Service ở bước này chỉ dùng
         * performGlobalAction().
         */
    }

    override fun onInterrupt() {

        Log.d(
            TAG,
            "Dich vu truy cap bi interrupt"
        )
    }

    override fun onDestroy() {

        if (phienBanDangHoatDong === this) {
            phienBanDangHoatDong = null
        }

        Log.d(
            TAG,
            "Dich vu truy cap da dung"
        )

        super.onDestroy()
    }

    /**
     * Thực hiện HOME thật của Android.
     */
    private fun thucThiHomeNoiBo(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_HOME
        )
    }

    companion object {

        private const val TAG =
            "DichVuTruyCap"

        @Volatile
        private var phienBanDangHoatDong:
                DichVuTruyCapFaceAccess? = null

        /**
         * Cho tầng thực thi bên ngoài biết
         * AccessibilityService đã được người dùng bật hay chưa.
         */
        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
        }

        /**
         * Yêu cầu Android thực hiện HOME.
         *
         * true:
         * yêu cầu đã được AccessibilityService chấp nhận.
         *
         * false:
         * service chưa bật hoặc HOME thất bại.
         */
        fun thucThiHome(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiHomeNoiBo()
        }
    }
}