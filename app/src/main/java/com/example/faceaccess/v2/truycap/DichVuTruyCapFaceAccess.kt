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
         * FaceAccess hiện không cần đọc AccessibilityEvent.
         *
         * Service chỉ dùng performGlobalAction()
         * cho các thao tác điều hướng Android.
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


    // =========================================================
    // GLOBAL ACTIONS
    // =========================================================

    private fun thucThiHomeNoiBo(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_HOME
        )
    }


    private fun thucThiBackNoiBo(): Boolean {

        return performGlobalAction(
            GLOBAL_ACTION_BACK
        )
    }


    companion object {

        private const val TAG =
            "DichVuTruyCap"

        @Volatile
        private var phienBanDangHoatDong:
                DichVuTruyCapFaceAccess? = null


        fun dangHoatDong(): Boolean {

            return phienBanDangHoatDong != null
        }


        fun thucThiHome(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiHomeNoiBo()
        }


        fun thucThiBack(): Boolean {

            val dichVu =
                phienBanDangHoatDong
                    ?: return false

            return dichVu
                .thucThiBackNoiBo()
        }
    }
}